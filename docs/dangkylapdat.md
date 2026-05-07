Sử dụng API cho Register (Đăng ký)

Nhập thông tin đăng ký

POST https://localhost:8080/cm-portlet/api/register_add

Ở đây: tham số là cấu trúc JSON 
"nam": "Nguyen Van A",
"phone": "0981234567",
"waterMeterAddress": "Phu My, Ba ria Vung tau",
"email": “test@gmail.com”




Cần cung cấp thông tin HEADER cho API. Cụ thể cần cung cấp giá trị cho Authorization là “Basic <chuỗi mã hoá base64>” (chuỗi mã hoá base64 được sinh ra bằng cách lấy username ghép với password và được mã hoá dạng Base64. 
Ví dụ: username là thanhdt@toctienltd.vn password là 123456 thì chuỗi cần mã hoá là “thanhdt@toctienltd.vn:123456”. Sau khi mã hoá, kết quả là “dGhhbmhkdEB0b2N0aWVubHRkLnZuOjEyMzQ1Ng==”


Kết quả: 
retCode: Mã lỗi
retMsg: Thông báo lỗi


Ví dụ:
curl -d "{
\"name\":\"Nguyen Van A\",\"phone\":\"0981234567\",\"waterMeterAddress\":\"Phu My Ba ria Vung Tau\",\"email\":\"test@gmail.com\"}" -H "Content-Type: application/json" -H 'Authorization: dGhhbmhkdEB0b2N0aWVubHRkLnZuOjEyMzQ1Ng==' -X POST 
https://toctienltd.vn/cm-portlet/api/register_add

Kết quả trả về: Khi thêm thành công.
{
    "retCode": "ERR_OK",
    "retMsg": "Successfull",
}


Nhận thông tin thời gian đăng ký theo số điện thoại
GET https://toctienltd.vn/cm-portlet/api/register_get/{phone}

Ở đây:
phone là Số điện thoại. Dạng số, gồm 10 ký tự, ví dụ: 0981234567
Cần cung cấp thông tin HEADER cho API. Cụ thể cần cung cấp giá trị cho Authorization là “Basic <chuỗi mã hoá base64>” (chuỗi mã hoá base64 được sinh ra bằng cách lấy username ghép với password và được mã hoá dạng Base64. 
Ví dụ: username là thanhdt@toctienltd.vn password là 123456 thì chuỗi cần mã hoá là “thanhdt@toctienltd.vn:123456”. Sau khi mã hoá, kết quả là “dGhhbmhkdEB0b2N0aWVubHRkLnZuOjEyMzQ1Ng==”


Kết quả: 
String: thời gian đăng ký

Ví dụ:
curl "https://toctienltd.vn/cm-portlet/api/register_get/0981234567"

Kết quả trả về:
“2026/05/06 – 21:30:01”

Một số mã lỗi và thông báo lỗi 

Khi thiếu hoặc sai các thông tin, API sẽ trả về Mã lỗi và thông báo lỗi sau.


