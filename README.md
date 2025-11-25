# Hướng dẫn chạy
--> Máy đã cài Docker desktop

# Với container của BE 
+ "cd" vào thư mục chứa Dockerfile
+ chạy command:
- Build image
docker build -t spring-api .
- Run container:
docker run -p 8080:8080 --name spring-container spring-api

# Với container của FE
+ cd vào thư mục chứa Dockerfile
- Build image
docker build -t react-ui .
- Run container:
docker run -p 3000:80 --name ui react-ui

# Sau khi build image thì có thể chạy trong docker desktop trong tab container nhưng khuyến nghị lần đầu chạy nên chạy bằng command

# Cách test với DB thật (Postgre) 
-> Chọn Run service "postgre-app" và "adminer" trong docker-compose.yml với cả vsc và intelliJ
- adminer để xem dữ liệu bằng các truy cập http://localhost:8081
+ Nhập các Tab như sau:
-> "System" : PostgreSQL 
-> Server: postgre-app
-> Username: username
-> Password: password
-> Database: product_db
Chọn Permanent login để ghi nhớ đăng nhập
Lưu ý vào docker desktop thấy container của postgre chạy thì mới mở adminer (mặc dù config ròi) hoặc chạy command "docker ps"
