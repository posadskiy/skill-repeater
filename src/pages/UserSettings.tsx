import { Container, NumberInput, Paper, Stack, TextInput, Title, Button } from '@mantine/core';
import { ThemeProvider } from '@mui/material/styles';
import { useEffect, useState } from 'react';
import { useMutation, useQuery } from '@tanstack/react-query';
import { UserService } from 'user-component-react';
import { skillsApi } from '../api/skills';
import { USER_URL } from '../api/client';

export function UserSettings() {
  const token = localStorage.getItem('token');
  const userId = localStorage.getItem('userId');
  const [zoneId, setZoneId] = useState('UTC');
  const [reminderHour, setReminderHour] = useState<number | ''>(9);

  const settingsQuery = useQuery({
    queryKey: ['settings', userId],
    queryFn: () => skillsApi.getSettings(),
    enabled: !!token && !!userId
  });

  useEffect(() => {
    if (settingsQuery.data) {
      setZoneId(settingsQuery.data.zoneId);
      setReminderHour(settingsQuery.data.reminderHour);
    }
  }, [settingsQuery.data]);

  const saveSettings = useMutation({
    mutationFn: () => skillsApi.saveSettings({
      zoneId,
      reminderHour: reminderHour === '' ? 9 : reminderHour
    })
  });

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
      <Stack gap="lg">
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
        <Paper radius="md" p="xl" withBorder>
          <Title order={3} mb="md">Reminders</Title>
          <Stack>
            <TextInput
              label="Time zone"
              description="IANA zone, for example Europe/Berlin"
              value={zoneId}
              onChange={(event) => setZoneId(event.currentTarget.value)}
            />
            <NumberInput
              label="Reminder hour"
              description="Local hour when a due-skill email may be sent"
              min={0}
              max={23}
              value={reminderHour}
              onChange={(value) => setReminderHour(typeof value === 'string' ? '' : value)}
            />
            <Button loading={saveSettings.isPending} onClick={() => saveSettings.mutate()}>
              Save reminders
            </Button>
          </Stack>
        </Paper>
      </Stack>
    </Container>
  );
} 
