package com.group07.roombooking.repository;

import com.google.gson.reflect.TypeToken;
import com.group07.roombooking.model.Room;
import com.group07.roombooking.utils.JsonFileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * Tầng Repository phụ trách đọc/ghi dữ liệu Phòng học nhóm từ file backend/data/rooms.json.
 * Trả về danh sách kiểu trừu tượng List<Room> (các phần tử bên trong là NormalRoom, ProjectorRoom, SeminarRoom).
 */
@Repository
public class RoomRepository {

    private static final Type ROOM_LIST_TYPE = new TypeToken<List<Room>>() {}.getType();
    private final Path filePath;

    public RoomRepository(@Value("${app.data.dir:data}") String dataDir) {
        this.filePath = Paths.get(dataDir, "rooms.json");
    }

    public List<Room> findAll() {
        return JsonFileUtils.readListFromFile(filePath, ROOM_LIST_TYPE);
    }

    public Optional<Room> findById(String roomId) {
        if (roomId == null) {
            return Optional.empty();
        }
        return findAll().stream()
                .filter(room -> room.getId().equalsIgnoreCase(roomId.trim()))
                .findFirst();
    }

    public void saveAll(List<Room> rooms) {
        JsonFileUtils.writeListToFileAtomically(filePath, rooms, ROOM_LIST_TYPE);
    }
}
