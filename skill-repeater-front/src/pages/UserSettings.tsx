import { Container, Title } from '@mantine/core';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { UserService } from 'user-component-react';
import { USER_URL } from '../api/client';

const accountTheme = createTheme();

export function UserSettings() {
  const token = localStorage.getItem('token');
  const userId = localStorage.getItem('userId');

  const handleError = (error: Error) => {
    console.error('Error in user settings:', error);
  };

  const handleSuccess = (message: string) => {
    console.log('Success in user settings:', message);
  };

  if (!token || !userId) {
    return null;
  }

  return (
    <Container size="sm" px="xs" py="md">
      <Title order={2} mb="md">Account</Title>
      <ThemeProvider theme={accountTheme}>
        <UserService
          apiUrl={USER_URL!}
          userId={userId}
          bearerToken={token}
          onError={handleError}
          onSuccess={handleSuccess}
          sx={{ mt: 0, maxWidth: '100%' }}
        />
      </ThemeProvider>
    </Container>
  );
}
