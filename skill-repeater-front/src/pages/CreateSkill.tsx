import { useState } from 'react';
import { Alert, Container, Paper, Title, TextInput, Textarea, Select, Button, Stack, Text, NumberInput, SimpleGrid, Checkbox } from '@mantine/core';
import { useNavigate } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { problemMessage } from '../api/problem';
import { skillsApi } from '../api/skills';
import { PRIORITY_SELECT_OPTIONS, type Period, type Priority } from '../types/api';

export function CreateSkill() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const userId = localStorage.getItem('userId');

  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [period, setPeriod] = useState<Period>('DAYS');
  const [number, setNumber] = useState<number | ''>(1);
  const [priority, setPriority] = useState<Priority>('MEDIUM');
  const [learnNow, setLearnNow] = useState(false);

  const addMutation = useMutation({
    mutationFn: () => skillsApi.add({
      name,
      description,
      period,
      number: number === '' ? 1 : number,
      priority,
      learnNow
    }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['skills'] });
      navigate('/skills');
    }
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    addMutation.mutate();
  };

  if (!userId) {
    return (
      <Container size="xs" px="xs">
        <Text c="red">Please log in with your User ID and token</Text>
      </Container>
    );
  }

  return (
    <Container size="xs" px="xs">
      <Paper radius="md" p="md" withBorder>
        <Title ta="center" mb="md">Create Skill</Title>

        <form onSubmit={handleSubmit}>
          <Stack>
            {addMutation.isError && (
              <Alert color="red" role="alert">
                {problemMessage(addMutation.error)}
              </Alert>
            )}
            <TextInput
              required
              label="Name"
              placeholder="Skill name"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />

            <Textarea
              required
              label="Description"
              placeholder="Describe your skill"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />

            <SimpleGrid cols={{ base: 1, xs: 2 }} spacing="sm">
              <NumberInput
                required
                label="Repeat Every"
                placeholder="Enter number"
                min={1}
                value={number}
                onChange={(value) => setNumber(typeof value === 'string' ? '' : value)}
              />

              <Select
                required
                label="Period"
                placeholder="Select period"
                data={[
                  { value: 'HOURS', label: 'Hours' },
                  { value: 'DAYS', label: 'Days' },
                  { value: 'WEEKS', label: 'Weeks' },
                  { value: 'MONTHS', label: 'Months' },
                  { value: 'YEARS', label: 'Years' }
                ]}
                value={period}
                onChange={(value) => setPeriod(value as Period)}
              />
            </SimpleGrid>

            <Select
              required
              label="Priority"
              description="How important this skill is to practice"
              data={PRIORITY_SELECT_OPTIONS}
              value={priority}
              onChange={(value) => setPriority((value as Priority) ?? 'MEDIUM')}
            />

            <Checkbox
              label="Learn now"
              description="The first review is due immediately"
              checked={learnNow}
              onChange={(event) => setLearnNow(event.currentTarget.checked)}
            />

            <Button type="submit" loading={addMutation.isPending} mt="md">
              Create Skill
            </Button>
          </Stack>
        </form>
      </Paper>
    </Container>
  );
} 