import axios from 'axios';

// Get API URLs from environment variables
export const API_URL = import.meta.env.VITE_API_URL;
export const AUTH_URL = import.meta.env.VITE_AUTH_URL;
export const USER_URL = import.meta.env.VITE_USER_URL;

// Validate that environment variables are set
if (!API_URL || !AUTH_URL || !USER_URL) {
  throw new Error('Required environment variables VITE_API_URL, VITE_AUTH_URL, and VITE_USER_URL must be set');
}

// Create axios instance for main API
export const apiClient = axios.create({
  baseURL: API_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Create axios instance for auth API
export const authClient = axios.create({
  baseURL: AUTH_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Create axios instance for user API
export const userClient = axios.create({
  baseURL: USER_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor to include auth token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add response interceptor to handle auth errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      localStorage.removeItem('token');
      localStorage.removeItem('userId');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Add request interceptor for auth client
authClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add request interceptor for user client
userClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export function subjectOf(accessToken: string): string {
  const payloadSegment = accessToken.split('.')[1];
  if (!payloadSegment) {
    throw new Error('Access token has no payload');
  }
  const base64 = payloadSegment.replace(/-/g, '+').replace(/_/g, '/');
  const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=');
  const payload = JSON.parse(atob(padded)) as { sub?: unknown };
  if (typeof payload.sub !== 'string' || payload.sub.length === 0) {
    throw new Error('Access token has no subject');
  }
  return payload.sub;
}

const storedToken = localStorage.getItem('token');
if (storedToken) {
  try {
    localStorage.setItem('userId', subjectOf(storedToken));
  } catch {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
  }
} 