import { Container } from '@mantine/core';
import { Register as AuthRegister } from 'auth-component-react';
import { useNavigate } from 'react-router-dom';
import { AUTH_URL, USER_URL } from '../api/client';

function serviceUrl(base: string, path: string) {
  return `${base.replace(/\/$/, '')}${path}`;
}

export function Register() {
  const navigate = useNavigate();

  return (
    <Container size="xs" px="md" py="xl" mih="100dvh" style={{ display: 'flex', alignItems: 'center' }}>
      <AuthRegister
        apiUrl={serviceUrl(USER_URL!, '/signup')}
        oauthApiUrl={AUTH_URL!.replace(/\/$/, '')}
        oauthReturnTo="/oauth/callback"
        providers={['apple', 'google']}
        showLoginLink
        onSwitchToLogin={() => navigate('/login')}
        onSuccess={() => navigate('/login')}
        style={{ width: '100%' }}
      />
    </Container>
  );
}
