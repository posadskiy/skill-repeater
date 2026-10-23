import { Container, Card, Text, Button, Group, Stack, Title, SimpleGrid } from '@mantine/core';
import { useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { skillsApi } from '../api/skills';
import { priorityLabel } from '../types/api';

export function Skills() {
  const navigate = useNavigate();
  const userId = localStorage.getItem('userId');

  const { data: due } = useQuery({
    queryKey: ['skills-due', userId],
    queryFn: () => skillsApi.due(),
    enabled: !!userId
  });

  const { data: skills, isLoading } = useQuery({
    queryKey: ['skills', userId],
    queryFn: () => skillsApi.getAll(),
    enabled: !!userId
  });

  if (isLoading) {
    return (
      <Container size="xs" px="xs">
        <Text>Loading...</Text>
      </Container>
    );
  }

  if (!userId) {
    return (
      <Container size="xs" px="xs">
        <Text c="red">Please log in with your User ID and token</Text>
      </Container>
    );
  }

  return (
    <Container size="xs" px="xs">
      <Stack gap="md">
        <Group justify="space-between" align="center" wrap="wrap">
          <Title order={2}>Skills</Title>
          <Button onClick={() => navigate('/skills/create')}>Create Skill</Button>
        </Group>

        <Title order={3}>Due now ({due?.length ?? 0})</Title>
        <SimpleGrid cols={{ base: 1, sm: 2 }} spacing="md">
          {due?.map((skill) => (
            <Card key={`due-${skill.id}`} withBorder>
              <Stack gap="xs">
                <Title order={4}>{skill.name}</Title>
                <Text size="sm" c="dimmed">Priority: {priorityLabel(skill.priority)}</Text>
                <Button variant="light" onClick={() => navigate(`/skills/${skill.id}`)}>Review</Button>
              </Stack>
            </Card>
          ))}
        </SimpleGrid>

        <Title order={3}>All skills</Title>
        <SimpleGrid cols={{ base: 1, sm: 2 }} spacing="md">
          {skills?.map((skill) => (
            <Card key={skill.id} withBorder>
              <Stack gap="xs">
                <Title order={3}>{skill.name}</Title>
                <Text size="sm" c="dimmed">Level: {skill.level} · Priority: {priorityLabel(skill.priority)}</Text>
                <Text size="sm" c="dimmed" lineClamp={2}>{skill.description}</Text>
                <Text size="sm" c="dimmed">
                  Next: {skill.nextRepeated ? new Date(skill.nextRepeated).toLocaleDateString(undefined, { month: 'short', day: 'numeric' }) : '—'}
                </Text>
                <SimpleGrid cols={2} spacing="xs">
                  <Button variant="light" fullWidth onClick={() => navigate(`/skills/${skill.id}`)}>
                    Details
                  </Button>
                  <Button variant="light" color="blue" fullWidth onClick={() => navigate(`/skills/${skill.id}/edit`)}>
                    Edit
                  </Button>
                </SimpleGrid>
              </Stack>
            </Card>
          ))}
        </SimpleGrid>
      </Stack>
    </Container>
  );
} 
