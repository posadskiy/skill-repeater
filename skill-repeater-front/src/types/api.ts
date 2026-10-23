export type Period = 'HOURS' | 'DAYS' | 'WEEKS' | 'MONTHS' | 'YEARS';

/** Matches backend `com.posadskiy.skillrepeater.api.model.Priority` */
export type Priority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export const PRIORITY_SELECT_OPTIONS: { value: Priority; label: string }[] = [
  { value: 'LOW', label: 'Low' },
  { value: 'MEDIUM', label: 'Medium' },
  { value: 'HIGH', label: 'High' },
  { value: 'CRITICAL', label: 'Critical' }
];

export function priorityLabel(p: Priority | undefined): string {
  if (!p) return '—';
  return PRIORITY_SELECT_OPTIONS.find((o) => o.value === p)?.label ?? p;
}

export type ReviewRating = 'AGAIN' | 'HARD' | 'GOOD' | 'EASY';

export interface Skill {
  id: string;
  name: string;
  description: string | null;
  level: number;
  lastRepeated: string | null;
  nextRepeated: string | null;
  userId: string;
  period: Period;
  number: number;
  priority: Priority;
  intervalSeconds?: number | null;
  learnNow?: boolean | null;
}

export type CreateSkillInput = {
  name: string;
  description: string;
  period: Period;
  number: number;
  priority: Priority;
  learnNow?: boolean;
};

export type UpdateSkillInput = {
  name: string;
  description: string;
  period: Period;
  number: number;
  priority: Priority;
};

export interface Page<T> {
  items: T[];
  page: number;
  size: number;
  total: number;
}

export interface User {
  id: string;
  username: string;
  email: string;
}

export interface AuthResponse {
  access_token: string;
  expires_in: number;
  username: string;
}

export interface RepeatHistory {
  id: string;
  skillId: string;
  userId: string;
  comment: string | null;
  repeatedAt: string;
  rating: ReviewRating | null;
  intervalSeconds: number | null;
  level: number | null;
} 
