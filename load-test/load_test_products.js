import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

export const options = {
    vus: 5, // virtual users
    duration: '1m', // run for 1 minute
}

const BASE_URL = 'http://localhost:8080';
const PASGE_SIZE = 10;

export default function () {
    const url = `${BASE_URL}/api/v1/products?page=0&size=${PASGE_SIZE}`;

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmYWRpYSIsImlhdCI6MTczNTk5MTY1MywiZXhwIjoxNzM1OTk1MjUzfQ.f4m4YZkZPovNb3uE_vO1upx1RPgFhSVnZKowT_YvwyWkdMWxnx4tSHCpJaY3AdN_orI0EIxBDFy-jEKJ41MeKQ',
        }
    }

    const response = http.get(url, params);

    check(response, {
        'status is 200': (r) => r.status === 200,
        'rate limit not exceeded': (r) => r.status !== 429,
    });

    // log the response status and time
    console.log(`Status: ${response.status} - ${response.timings.duration}ms`);

    // short pause between requests
    sleep(0.1);
};