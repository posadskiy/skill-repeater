import { OAuthCallback as AuthCallback } from 'auth-component-react';
import { useNavigate } from 'react-router-dom';
import { saveSession } from '../api/auth';
import { AUTH_URL } from '../api/client';

export function OAuthCallback() {
  const navigate = useNavigate();

  return (
    <AuthCallback
      oauthApiUrl={AUTH_URL!.replace(/\/$/, '')}
      returnPath="/"
      onSuccess={(tokens) => {
        saveSession(tokens.access_token);
        navigate('/');
      }}
      onError={(error) => {
        console.error('OAuth sign-in failed:', error);
      }}
    />
  );
}
