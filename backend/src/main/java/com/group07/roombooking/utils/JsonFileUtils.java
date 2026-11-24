package com.group07.roombooking.utils;

import com.google.gson.*;
import com.group07.roombooking.exception.DataStorageException;
import com.group07.roombooking.model.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Tiện ích đọc và ghi file JSON sử dụng thư viện Google Gson.
 * Đáp ứng yêu cầu kỹ thuật:
 * 1. Hỗ trợ Polymorphism khi đọc/ghi abstract class Room (NormalRoom, ProjectorRoom, SeminarRoom).
 * 2. Không bao giờ coi file JSON bị hỏng là danh sách rỗng rồi âm thầm ghi đè.
 * 3. Ghi file an toàn qua file tạm (.tmp) rồi di chuyển nguyên tử (Atomic Move) sang file đích.
 */
public final class JsonFileUtils {

    private static final DateTimeFormatter ISO_LOCAL_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]");
    private static final DateTimeFormatter OUTPUT_MINUTE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Room.class, new RoomPolymorphicAdapter())
            .create();

    private JsonFileUtils() {
    }

    public static Gson getGson() {
        return GSON;
    }

    public static <T> List<T> readListFromFile(Path filePath, Type listType) {
        if (!Files.exists(filePath)) {
            throw new DataStorageException(
                    "DATA_FILE_NOT_FOUND",
                    "Không tìm thấy file dữ liệu JSON tại đường dẫn: " + filePath.toAbsolutePath()
            );
        }
        try {
            String content = Files.readString(filePath, StandardCharsets.UTF_8);
            if (content == null || content.trim().isEmpty()) {
                throw new DataStorageException(
                        "CORRUPTED_JSON_DATA",
                        "File dữ liệu JSON bị rỗng hoặc hỏng định dạng: " + filePath.getFileName()
                );
            }
            List<T> result = GSON.fromJson(content, listType);
            if (result == null) {
                throw new DataStorageException(
                        "CORRUPTED_JSON_DATA",
                        "Không thể phân tích cú pháp danh sách từ file JSON: " + filePath.getFileName()
                );
            }
            return result;
        } catch (JsonParseException e) {
            throw new DataStorageException(
                    "CORRUPTED_JSON_DATA",
                    "Dữ liệu file JSON bị hỏng định dạng (" + filePath.getFileName() + "): " + e.getMessage(),
                    e
            );
        } catch (IOException e) {
            throw new DataStorageException(
                    "FILE_IO_ERROR",
                    "Lỗi đọc file dữ liệu JSON (" + filePath.getFileName() + "): " + e.getMessage(),
                    e
            );
        }
    }

    public static <T> void writeListToFileAtomically(Path filePath, List<T> data, Type listType) {
        try {
            Path parentDir = filePath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }
            String jsonContent = GSON.toJson(data, listType);
            Path tempFile = filePath.resolveSibling(filePath.getFileName().toString() + ".tmp");

            Files.writeString(
                    tempFile,
                    jsonContent,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );

            try {
                Files.move(tempFile, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(tempFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new DataStorageException(
                    "FILE_WRITE_ERROR",
                    "Không thể lưu dữ liệu vào file JSON (" + filePath.getFileName() + "): " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * TypeAdapter tùy chỉnh cho LocalDateTime theo đúng chuẩn ISO địa phương (Asia/Ho_Chi_Minh).
     */
    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(OUTPUT_MINUTE_FORMATTER));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String text = json.getAsString();
            try {
                return LocalDateTime.parse(text, ISO_LOCAL_FORMATTER);
            } catch (Exception e) {
                throw new JsonParseException("Định dạng thời gian không hợp lệ: " + text, e);
            }
        }
    }

    /**
     * TypeAdapter đa hình (Polymorphic Adapter) giúp Gson ánh xạ chính xác trường "type" trong rooms.json
     * thành các đối tượng con NormalRoom, ProjectorRoom, SeminarRoom.
     */
    private static class RoomPolymorphicAdapter implements JsonSerializer<Room>, JsonDeserializer<Room> {
        @Override
        public JsonElement serialize(Room src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", src.getId());
            obj.addProperty("name", src.getName());
            obj.addProperty("floor", src.getFloor());
            obj.addProperty("capacity", src.getCapacity());
            obj.addProperty("type", src.getRoomType().name());
            obj.addProperty("status", src.getStatus().name());
            return obj;
        }

        @Override
        public Room deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            String id = getRequiredString(obj, "id");
            String name = getRequiredString(obj, "name");
            int floor = obj.get("floor").getAsInt();
            int capacity = obj.get("capacity").getAsInt();
            String typeStr = getRequiredString(obj, "type").toUpperCase();
            String statusStr = getRequiredString(obj, "status").toUpperCase();

            RoomStatus status = RoomStatus.valueOf(statusStr);
            RoomType roomType = RoomType.valueOf(typeStr);

            return switch (roomType) {
                case NORMAL -> new NormalRoom(id, name, floor, capacity, status);
                case PROJECTOR -> new ProjectorRoom(id, name, floor, capacity, status);
                case SEMINAR -> new SeminarRoom(id, name, floor, capacity, status);
            };
        }

        private String getRequiredString(JsonObject obj, String key) {
            if (!obj.has(key) || obj.get(key).isJsonNull()) {
                throw new JsonParseException("Thiếu trường bắt buộc trong JSON của Room: " + key);
            }
            return obj.get(key).getAsString();
        }
    }
}
