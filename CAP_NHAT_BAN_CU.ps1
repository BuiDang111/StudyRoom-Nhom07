$ErrorActionPreference = 'Stop'
Write-Host 'Hay tat cua so Java cua ban cu truoc khi cap nhat.'
$oldRoot = (Read-Host 'Dan duong dan thu muc StudyRoom CU (khong phai thu muc ban moi)').Trim().Trim('"')
$runtime = Join-Path $oldRoot 'runtime'
$studentsFile = Join-Path $runtime 'data/students.json'
$bookingsFile = Join-Path $runtime 'data/bookings.json'
if (!(Test-Path $studentsFile) -or !(Test-Path $bookingsFile)) { throw 'Khong tim thay data/students.json va bookings.json trong runtime cua ban cu.' }
if ([IO.Path]::GetFullPath($oldRoot) -eq [IO.Path]::GetFullPath($PSScriptRoot)) { throw 'Hay chon thu muc BAN CU, khong chon ban moi.' }
$oldStudents = @(Get-Content -Raw -Encoding UTF8 $studentsFile | ConvertFrom-Json)
$bookings = @(Get-Content -Raw -Encoding UTF8 $bookingsFile | ConvertFrom-Json)
$newStudents = @(Get-Content -Raw -Encoding UTF8 (Join-Path $PSScriptRoot 'runtime/data/students.json') | ConvertFrom-Json)
$referenced = @($bookings | ForEach-Object { $_.studentId })
$ids = @($newStudents | ForEach-Object { $_.id })
$legacy = @($oldStudents | Where-Object { $_.id -in $referenced -and $_.id -notin $ids })
$merged = @($newStudents) + @($legacy)
$backup = Join-Path $runtime ('backup-' + (Get-Date -Format 'yyyyMMdd-HHmmss'))
New-Item -ItemType Directory -Path $backup | Out-Null
Copy-Item (Join-Path $runtime 'data') $backup -Recurse
Copy-Item (Join-Path $runtime 'studyroom.jar') $backup
Copy-Item (Join-Path $PSScriptRoot 'runtime/studyroom.jar') (Join-Path $runtime 'studyroom.jar') -Force
$json = ConvertTo-Json -InputObject @($merged) -Depth 10
[IO.File]::WriteAllText($studentsFile, $json, (New-Object System.Text.UTF8Encoding($false)))
Write-Host 'Da cap nhat. Lich dat va rooms.json khong bi thay doi. Ban sao luu:' $backup
Write-Host 'Chay lai START_WINDOWS.bat trong thu muc BAN CU, sau do Ctrl+F5 tren trinh duyet.'
Write-Host 'Sinh vien mau co lich cu duoc giu lai de bao toan lich su; khong tu gan lich do cho thanh vien moi.'
