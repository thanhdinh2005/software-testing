import http from 'k6/http';
import { check, group, sleep } from 'k6';

const BASE_URL = 'http://host.docker.internal:8080/api';
const userCredentials = {
    username: 'user122',
    password: 'password123'
};

// --- Cấu hình Load Test (100 -> 500 -> 1000 VUs) ---
export const options = {
    thresholds: {
        // Ngưỡng thất bại chung (Tỷ lệ lỗi dưới 1%)
        'http_req_failed': ['rate<0.01'],
        // Ngưỡng thời gian phản hồi cho API lấy danh sách sản phẩm
        'http_req_duration{tag:GetProducts}': ['p(95)<400'],
        // Ngưỡng thời gian phản hồi cho API lấy sản phẩm chi tiết
        'http_req_duration{tag:GetSingleProduct}': ['p(95)<200'],
    },
    stages: [
        { duration: '30s', target: 100 },
        { duration: '1m', target: 100 },
        { duration: '30s', target: 500 },
        { duration: '1m', target: 500 },
        { duration: '30s', target: 1000 },
        { duration: '1m', target: 1000 },
        { duration: '10s', target: 0 },
    ],
};

export function setup() {
    // Yêu cầu Login
    const loginRes = http.post(`${BASE_URL}/auth/login`, JSON.stringify(userCredentials), {
        headers: { 'Content-Type': 'application/json' }
    });

    const body = loginRes.json();
    check(loginRes, {
        'Login success in setup': (r) => r.status === 200,
    });

    return { token: body.data.token };
}

export default function (data) {
    if (!data.token) {
        sleep(1);
        return;
    }

    const headers = {
        'Authorization': `Bearer ${data.token}`,
        'Content-Type': 'application/json',
    };

    group('Product Test Flow', function () {
        let productRes = http.get(`${BASE_URL}/products`, {
            headers: headers,
            tags: { name: 'GetProducts' }
        });
        check(productRes, { 'Products List OK': (r) => r.status === 200 });

        let singleRes = http.get(`${BASE_URL}/products/1`, {
            headers: headers,
            tags: { name: 'GetSingleProduct' }
        });
        check(singleRes, { 'Single Product OK': (r) => r.status === 200 });
    });

    sleep(1);
}