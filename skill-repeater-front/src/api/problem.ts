import axios from 'axios';

type ProblemBody = {
  detail?: unknown;
  message?: unknown;
  errors?: { name?: unknown; reason?: unknown }[];
};

export function problemMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data as ProblemBody | undefined;
    if (data && typeof data === 'object') {
      const reasons = Array.isArray(data.errors)
        ? data.errors
            .map((item) => (typeof item.reason === 'string' ? item.reason : ''))
            .filter((reason) => reason.length > 0)
        : [];
      if (reasons.length > 0) {
        return reasons.join(' ');
      }
      if (typeof data.detail === 'string' && data.detail.length > 0 && data.detail !== 'Internal Server Error') {
        return data.detail;
      }
    }
    if (!error.response) {
      return "Can't reach the server. Check your connection and try again.";
    }
  }
  return 'Something went wrong. Please try again.';
}
