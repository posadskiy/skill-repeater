import { useParams, useNavigate } from 'react-router-dom';
import { Alert, Container, Card, Text, Button, Group, SimpleGrid, Stack, Title } from '@mantine/core';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { problemMessage } from '../api/problem';
import { skillsApi } from '../api/skills';
import { priorityLabel, type ReviewRating } from '../types/api';

export function SkillDetails() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const { data: skill, isLoading } = useQuery({
    queryKey: ['skill', id],
    queryFn: () => skillsApi.getById(id!),
    enabled: !!id
  });

  const reviewMutation = useMutation({
    mutationFn: (rating: ReviewRating) => skillsApi.review(id!, rating),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['skill', id] });
      queryClient.invalidateQueries({ queryKey: ['skill-history', id] });
      queryClient.invalidateQueries({ queryKey: ['skills'] });
      queryClient.invalidateQueries({ queryKey: ['skills-due'] });
    }
  });

  const { data: history } = useQuery({
    queryKey: ['skill-history', id],
    queryFn: () => skillsApi.getHistory(id!),
    enabled: !!id
  });

  if (isLoading) {
    return (
      <Container size="xs" px="xs">
        <Text>Loading...</Text>
      </Container>
    );
  }

  if (!skill) {
    return (
      <Container size="xs" px="xs">
        <Text c="red">Skill not found</Text>
      </Container>
    );
  }

  return (
    <Container size="xs" px="xs">
      <Stack gap="md">
        {reviewMutation.isError && (
          <Alert color="red" role="alert">
            {problemMessage(reviewMutation.error)}
          </Alert>
        )}
        <Group justify="space-between" align="center" wrap="wrap">
          <Title order={2} style={{ overflowWrap: 'anywhere' }}>{skill.name}</Title>
          <Button variant="light" onClick={() => navigate(`/skills/${id}/edit`)}>
            Edit
          </Button>
        </Group>
        <SimpleGrid cols={2} spacing="xs">
          <Button color="red" fullWidth onClick={() => reviewMutation.mutate('AGAIN')} loading={reviewMutation.isPending}>Again</Button>
          <Button color="orange" fullWidth onClick={() => reviewMutation.mutate('HARD')} loading={reviewMutation.isPending}>Hard</Button>
          <Button fullWidth onClick={() => reviewMutation.mutate('GOOD')} loading={reviewMutation.isPending}>Good</Button>
          <Button color="teal" fullWidth onClick={() => reviewMutation.mutate('EASY')} loading={reviewMutation.isPending}>Easy</Button>
        </SimpleGrid>

        <Card withBorder>
          <Stack gap="xs">
            <Text size="sm" c="dimmed">Level: {skill.level}</Text>
            <Text size="sm" c="dimmed">Priority: {priorityLabel(skill.priority)}</Text>
            <Text size="sm" c="dimmed">{skill.description}</Text>
            <Stack gap="xs">
              <Text size="lg" fw={500}>Next Repeat</Text>
              <Text size="sm" c="dimmed">
                {skill.nextRepeated ? `${new Date(skill.nextRepeated).toLocaleDateString(undefined, { month: 'short', day: 'numeric' })} at ${new Date(skill.nextRepeated).toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' })}` : '—'}
              </Text>
            </Stack>
          </Stack>
        </Card>

        <Card withBorder>
          <Stack gap="xs">
            <Text size="lg" fw={500}>Repeat History</Text>
            {history?.map((record) => {
              const date = new Date(record.repeatedAt);
              return (
                <Group key={record.id} justify="space-between" wrap="wrap" gap="xs">
                  <Text size="sm" c="dimmed">
                    {date.toLocaleDateString(undefined, { month: 'short', day: 'numeric' })}
                    {' · '}
                    {date.toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' })}
                  </Text>
                  <Text size="sm" c="dimmed">
                    {record.rating ?? '—'} · level {record.level ?? '—'}
                  </Text>
                </Group>
              );
            })}
          </Stack>
        </Card>
      </Stack>
    </Container>
  );
} 
