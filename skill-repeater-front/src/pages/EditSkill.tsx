import { useState, useEffect } from 'react';
import { Alert, Container, Paper, Title, TextInput, Textarea, Select, Button, Stack, Text, NumberInput, SimpleGrid, Modal } from '@mantine/core';
import { useNavigate, useParams } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { problemMessage } from '../api/problem';
import { skillsApi } from '../api/skills';
import { PRIORITY_SELECT_OPTIONS, type Period, type Priority } from '../types/api';

export function EditSkill() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const userId = localStorage.getItem('userId');
  const [deleteModalOpened, setDeleteModalOpened] = useState(false);

  const { data: skill, isLoading } = useQuery({
    queryKey: ['skill', id],
    queryFn: () => skillsApi.getById(id!),
    enabled: !!id
  });

  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [period, setPeriod] = useState<Period>('DAYS');
  const [number, setNumber] = useState<number | ''>(1);
  const [priority, setPriority] = useState<Priority>('MEDIUM');

  useEffect(() => {
    if (skill) {
      setName(skill.name);
      setDescription(skill.description ?? '');
      setPeriod(skill.period);
      setNumber(skill.number);
      setPriority(skill.priority ?? 'MEDIUM');
    }
  }, [skill]);

  const updateMutation = useMutation({
    mutationFn: () => skillsApi.update(id!, {
      name,
      description,
      period,
      number: number === '' ? 1 : number,
      priority
    }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['skills'] });
      navigate(`/skills/${id}`);
    }
  });

  const deleteMutation = useMutation({
    mutationFn: () => skillsApi.delete(id!),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['skills'] });
      navigate('/skills');
    }
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    updateMutation.mutate();
  };

  const handleDelete = () => {
    setDeleteModalOpened(true);
  };

  const confirmDelete = () => {
    deleteMutation.mutate();
    setDeleteModalOpened(false);
  };

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
      <Paper radius="md" p="md" withBorder>
        <Title ta="center" mb="md">Edit Skill</Title>

        <form onSubmit={handleSubmit}>
          <Stack>
            {(updateMutation.isError || deleteMutation.isError) && (
              <Alert color="red" role="alert">
                {problemMessage(updateMutation.isError ? updateMutation.error : deleteMutation.error)}
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

            <SimpleGrid cols={{ base: 1, xs: 2 }} spacing="sm" mt="md">
              <Button type="submit" fullWidth loading={updateMutation.isPending}>
                Update Skill
              </Button>
              <Button
                color="red"
                fullWidth
                loading={deleteMutation.isPending}
                onClick={handleDelete}
              >
                Delete Skill
              </Button>
            </SimpleGrid>
          </Stack>
        </form>
      </Paper>

      <Modal
        opened={deleteModalOpened}
        onClose={() => setDeleteModalOpened(false)}
        title="Delete Skill"
        centered
      >
        <Stack>
          <Text>Are you sure you want to delete this skill? This action cannot be undone.</Text>
          <SimpleGrid cols={2} spacing="sm" mt="md">
            <Button
              variant="outline"
              fullWidth
              onClick={() => setDeleteModalOpened(false)}
            >
              Cancel
            </Button>
            <Button
              color="red"
              fullWidth
              onClick={confirmDelete}
              loading={deleteMutation.isPending}
            >
              Delete
            </Button>
          </SimpleGrid>
        </Stack>
      </Modal>
    </Container>
  );
} 
