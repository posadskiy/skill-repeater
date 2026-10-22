import { Container, Paper, Title } from '@mantine/core';
import { ThemeProvider } from '@mui/material/styles';
import { UserService } from 'user-component-react';
import { USER_URL } from '../api/client';

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
    <Container size="md" py="xl">
      <Paper radius="md" p="xl" withBorder>
        <Title ta="center" mb="xl">User Settings</Title>
        <ThemeProvider theme={{}}>
          <UserService
            apiUrl={USER_URL!}
            userId={userId}
            bearerToken={token}
            onError={handleError}
            onSuccess={handleSuccess}
          />
        </ThemeProvider>
      </Paper>
    </Container>
  );
}
