import http from 'k6/http';
import { check, sleep } from 'k6';

const API_BASE_URL = 'http://host.docker.internal:8080/api/auth/login';
const userCredentials = {
    username: 'user12',
    password: 'password123'
};

export const options = {
  thresholds: {
    'http_req_failed': ['rate<0.5'], 
  },
  stages: [
    { duration: '5m', target: 2000 },
    { duration: '30s', target: 0 }, 
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
        'has token in response': (r) => body && body.data && typeof body.data.token === 'string' && body.data.token.length > 0,
    });

    sleep(0.5);
}