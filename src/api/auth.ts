import { apiClient, authClient, subjectOf } from './client';
import type { AuthResponse, User } from '../types/api';

const SESSION_EXPIRY_KEY = 'session_expiry';

export function saveSession(accessToken: string, expiresIn?: number) {
  localStorage.setItem('token', accessToken);
  localStorage.setItem('userId', subjectOf(accessToken));
  if (expiresIn != null) {
    localStorage.setItem(SESSION_EXPIRY_KEY, String(Date.now() + expiresIn * 1000));
  }
}

export const authApi = {
  login: async (username: string, password: string): Promise<AuthResponse> => {
    const response = await authClient.post<AuthResponse>('/login', { username, password });
    const { access_token, expires_in } = response.data;
    saveSession(access_token, expires_in);
    return response.data;
  },

  register: async (username: string, email: string, password: string): Promise<AuthResponse> => {
    const response = await authClient.post<AuthResponse>('/v0/auth/register', { username, email, password });
    return response.data;
  },

  getCurrentUser: async (): Promise<User> => {
    const { data } = await apiClient.get('/api/auth/me');
    return data;
  },

  logout: async () => {
    await authClient.post('/logout');
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem(SESSION_EXPIRY_KEY);
    window.location.href = '/login';
  },

  checkSessionExpiry: () => {
    const expiryTime = localStorage.getItem(SESSION_EXPIRY_KEY);
    if (!expiryTime) return true;
    
    const isExpired = Date.now() > parseInt(expiryTime);
    if (isExpired) {
      localStorage.removeItem('token');
      localStorage.removeItem('userId');
      localStorage.removeItem(SESSION_EXPIRY_KEY);
      window.location.href = '/login';
    }
    return isExpired;
  }
}; 