import http from 'k6/http';
import { check, sleep } from 'k6';

const API_BASE_URL = 'http://host.docker.internal:8080/api/auth/login';

// Dữ liệu người dùng mẫu
const userCredentials = {
    username: 'user1',
    password: 'password123'
};

// Cấu hình kiểm thử theo yêu cầu: 100 -> 500 -> 1000 VUs
export const options = {
  // Ngưỡng thất bại (Thresholds): Nếu bị vượt quá, bài test được đánh dấu là Fail
  thresholds: {
    'http_req_duration': ['p(95)<500'],
    // Tỷ lệ lỗi: Phải dưới 1%
    'http_req_failed': ['rate<0.01'],
  },
  // Định nghĩa các giai đoạn tải (Stages)
  stages: [
    // 1. Tải nhẹ (Ramp-up 100 VUs)
    { duration: '30s', target: 100 },
    // 2. Giữ tải 100 VUs
    { duration: '1m', target: 100 },

    // 3. Tải trung bình (Ramp-up 500 VUs)
    { duration: '30s', target: 500 },
    // 4. Giữ tải 500 VUs
    { duration: '1m', target: 500 },

    // 5. Tải cao (Ramp-up 1000 VUs)
    { duration: '30s', target: 1000 },
    // 6. Giữ tải 1000 VUs
    { duration: '1m', target: 1000 },

    // 7. Giảm tải (Ramp-down)
    { duration: '10s', target: 0 },
  ],
};

export default function () {
    const res = http.post(API_BASE_URL, JSON.stringify(userCredentials), {
        headers: {
            'Content-Type': 'application/json'
        },
        tags: { name: 'LoginEndpoint' }
    });

    const body = res.json();

    check(res, {
        'is status 200': (r) => r.status === 200,
        // Kiểm tra Token bằng cách truy cập body.data.token
        'has token in response': (r) => body && body.data && typeof body.data.token === 'string' && body.data.token.length > 0,
    });

    sleep(1);
}