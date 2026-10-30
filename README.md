### 4 tính năng AOP đã triển khai

**1. Chống đặt trùng đơn (`@CheckIdempotent(expireSecond = 10)`)**
- Sử dụng `ConcurrentHashMap` để lưu trữ tạm thời Idempotency Key trong RAM.
- Nếu phát hiện request sử dụng lại cùng một key trong vòng 10 giây, hệ thống sẽ ném `IllegalStateException` để ngăn chặn việc tạo đơn hàng trùng lặp.

**2. Làm sạch dữ liệu (`@SanitizeOrder`)**
- Sử dụng `@Around` để can thiệp trước khi hàm nghiệp vụ chạy.
- Tự động `trim()` khoảng trắng thừa giữa các chuỗi và tự động viết hoa mã giảm giá.

**3. Tự động ghi nhật ký (`@LogExecution(action = "Tạo đơn hàng")`)**
- Tự động ghi log các tham số đầu vào (Input).
- Tự động ghi log kết quả đầu ra (Output).

**4. Đo hiệu năng và cảnh báo nghẽn mạng (`@TrackTime(thresholdMs = 100)`)**
- Đo thời gian thực thi của hàm.
- Nếu thời gian thực thi vượt quá ngưỡng 100ms, hệ thống sẽ ghi log cảnh báo (`WARN`).
