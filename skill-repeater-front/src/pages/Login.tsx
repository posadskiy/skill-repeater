import { Container } from '@mantine/core';
import { Login as AuthLogin } from 'auth-component-react';
import { useNavigate } from 'react-router-dom';
import { saveSession } from '../api/auth';
import { AUTH_URL } from '../api/client';

function serviceUrl(base: string, path: string) {
  return `${base.replace(/\/$/, '')}${path}`;
}

export function Login() {
  const navigate = useNavigate();

  return (
    <Container size="xs" px="md" py="xl" mih="100dvh" style={{ display: 'flex', alignItems: 'center' }}>
      <AuthLogin
        apiUrl={serviceUrl(AUTH_URL!, '/login')}
        oauthApiUrl={AUTH_URL!.replace(/\/$/, '')}
        oauthReturnTo="/oauth/callback"
        providers={['apple', 'google']}
        showRegisterLink
        onSwitchToRegister={() => navigate('/register')}
        onSuccess={(response) => {
          saveSession(response.access_token, response.expires_in);
          navigate('/');
        }}
        style={{ width: '100%' }}
      />
    </Container>
  );
}
