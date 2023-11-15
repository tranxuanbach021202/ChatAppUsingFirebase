# ChatAppUsingFirebase
  Chat app sử dụng các thư viện của firebase để xác thực nhắn tin realtime, lưu trữ ảnh. Sử dụng thư viện bên thứ 3 là Zegocloud để thực hiện video call.
## Các công nghệ sử dụng 
- LiveData và ViewModel: Triển khai mô hình MVVM
- Kotlin coroutine, Flow
- Dagger Hilt
- Glide
- Thư viện bên thứ 3: SendGrid, Lottie, Zegocloud
## Cấu trúc dự án
- core: chứa các constantes
- di: chứa các module dependency injection
- data: Để thực hiện tương tác với firebase và local
- ui: lớp giao diện người dùng
- utils: các lớp dùng chung.

## Review

<details>
  <summary>Khi khởi động app có thể chọn đăng nhập hoặc đăng ký</summary>
  <img src="https://github.com/tranxuanbach021202/ChatAppUsingFirebase/blob/main/app/src/assets/Screenshot_20231115_094906.png" width="800">
</details>

<details>
  <summary>Đăng ký</summary>
  <p>Nhập các thông tin gồm email, mật khẩu và xác nhận lại mật khẩu, sau đó nhấn gửi mã OTP </p>
  <img src="https://github.com/tranxuanbach021202/ChatAppUsingFirebase/blob/main/app/src/assets/Screenshot_20231115_094819.png" width="800">

   <p>Nhập mã OTP nhận được từ gmail. </p>
  <img src="https://github.com/tranxuanbach021202/ChatAppUsingFirebase/blob/main/app/src/assets/386459850_310347868582636_6101623489070700826_n.png" width="800">
</details>

<details>
  <summary>Đăng nhập</summary>
  <img src="https://github.com/tranxuanbach021202/ChatAppUsingFirebase/blob/main/app/src/assets/Screenshot_20231115_094425.png" width="800">
</details>
<details>
  <summary>Màn home</summary>
  <p>Hiện tại đang không có ai online nên không hiển thị các người dùng online </p>
  <img src="https://github.com/tranxuanbach021202/ChatAppUsingFirebase/blob/main/app/src/assets/Screenshot_20231115_094557.png" width="800">
</details>

<details>
  <summary>Nhắn tin</summary>
  <img src="https://github.com/tranxuanbach021202/ChatAppUsingFirebase/blob/main/app/src/assets/Screenshot_20231115_094738.png" width="800">
</details>
<details>
  <summary>Gọi điện, video call</summary>
  <img src="https://github.com/tranxuanbach021202/ChatAppUsingFirebase/blob/main/app/src/assets/370635140_1038741750812508_1151008633796407443_n.png" width="800">
</details>

<details>
  <summary>Màn tìm kiếm bạn bè, xác nhận lời mời kết bạn</summary>
  <img src="https://github.com/tranxuanbach021202/ChatAppUsingFirebase/blob/main/app/src/assets/Screenshot_20231115_094706.png" width="800">
</details>

<details>
  <summary>Chỉnh sửa profile</summary>
  <img src="https://github.com/tranxuanbach021202/ChatAppUsingFirebase/blob/main/app/src/assets/Screenshot_20231115_094635.png" width="800">
</details>


