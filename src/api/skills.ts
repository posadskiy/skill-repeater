import { apiClient } from './client';
import type {
  CreateSkillInput,
  Page,
  RepeatHistory,
  ReviewRating,
  Skill,
  UpdateSkillInput,
  UserSettings
} from '../types/api';

export const skillsApi = {
  getAll: async (): Promise<Skill[]> => {
    const response = await apiClient.get<Page<Skill>>('/v0/skills', { params: { page: 0, size: 100 } });
    return response.data.items;
  },

  due: async (): Promise<Skill[]> => {
    const response = await apiClient.get<Skill[]>('/v0/skills/due');
    return response.data;
  },

  getById: async (id: string): Promise<Skill> => {
    const response = await apiClient.get<Skill>(`/v0/skills/${id}`);
    return response.data;
  },

  add: async (skill: CreateSkillInput): Promise<Skill> => {
    const response = await apiClient.post<Skill>('/v0/skills', skill);
    return response.data;
  },

  update: async (id: string, skill: UpdateSkillInput): Promise<Skill> => {
    const response = await apiClient.put<Skill>(`/v0/skills/${id}`, skill);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/v0/skills/${id}`);
  },

  review: async (id: string, rating: ReviewRating): Promise<Skill> => {
    const response = await apiClient.post<Skill>(`/v0/skills/${id}/reviews`, { rating });
    return response.data;
  },

  getHistory: async (skillId: string): Promise<RepeatHistory[]> => {
    const response = await apiClient.get<Page<RepeatHistory>>(`/v0/skills/${skillId}/history`, {
      params: { page: 0, size: 100 }
    });
    return response.data.items;
  },

  getSettings: async (): Promise<UserSettings> => {
    const response = await apiClient.get<UserSettings>('/v0/settings');
    return response.data;
  },

  saveSettings: async (settings: UserSettings): Promise<UserSettings> => {
    const response = await apiClient.put<UserSettings>('/v0/settings', settings);
    return response.data;
  }
};
