import { AppShell, Burger, Group, Text, UnstyledButton, Stack, rem, Button } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import {
  Navigate,
  Outlet,
  Route,
  createBrowserRouter,
  createRoutesFromElements,
  useLocation,
  useNavigate,
} from 'react-router-dom';
import { withFaroRouterInstrumentation } from '@grafana/faro-react';
import { CreateSkill } from './pages/CreateSkill';
import { EditSkill } from './pages/EditSkill';
import { Login } from './pages/Login';
import { OAuthCallback } from './pages/OAuthCallback';
import { Register } from './pages/Register';
import { SkillDetails } from './pages/SkillDetails';
import { Skills } from './pages/Skills';
import { UserSettings } from './pages/UserSettings';

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const location = useLocation();
  const token = localStorage.getItem('token');
  const userId = localStorage.getItem('userId');
  if (!token || !userId) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }
  return <>{children}</>;
}

function AppLayout() {
  const [opened, { toggle, close }] = useDisclosure(false);
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    navigate('/login');
  };

  return (
    <AppShell
      header={{ height: { base: 60, sm: 70 } }}
      navbar={{
        width: { base: '100%', sm: 300 },
        breakpoint: 'sm',
        collapsed: { mobile: !opened },
      }}
      padding={{ base: 'xs', sm: 'md' }}
    >
      <AppShell.Header>
        <Group h="100%" px="sm" justify="space-between" wrap="nowrap" gap="xs">
          <Group gap="xs" wrap="nowrap" style={{ minWidth: 0 }}>
            <Burger opened={opened} onClick={toggle} hiddenFrom="sm" size="sm" />
            <Text size="md" fw={700} truncate>
              Skill Repeater
            </Text>
          </Group>
          <Group gap={4} wrap="nowrap">
            <Button
              variant="light"
              size="compact-sm"
              onClick={() => {
                navigate('/settings');
                close();
              }}
            >
              Account
            </Button>
            <Button variant="subtle" size="compact-sm" onClick={handleLogout}>
              Log out
            </Button>
          </Group>
        </Group>
      </AppShell.Header>

      <AppShell.Navbar p={{ base: 'xs', sm: 'md' }}>
        <Stack gap="xs">
          <UnstyledButton
            onClick={() => {
              navigate('/skills');
              close();
            }}
            style={{
              padding: rem(8),
              borderRadius: rem(6),
              fontWeight: 500,
              textAlign: 'left',
            }}
          >
            Skills
          </UnstyledButton>
          <UnstyledButton
            onClick={() => {
              navigate('/skills/create');
              close();
            }}
            style={{
              padding: rem(8),
              borderRadius: rem(6),
              fontWeight: 500,
              textAlign: 'left',
            }}
          >
            Create Skill
          </UnstyledButton>
          <UnstyledButton
            onClick={() => {
              navigate('/settings');
              close();
            }}
            style={{
              padding: rem(8),
              borderRadius: rem(6),
              fontWeight: 500,
              textAlign: 'left',
            }}
          >
            Account
          </UnstyledButton>
        </Stack>
      </AppShell.Navbar>

      <AppShell.Main>
        <Outlet />
      </AppShell.Main>
    </AppShell>
  );
}

const routes = createRoutesFromElements(
  <>
    <Route path="/login" element={<Login />} />
    <Route path="/register" element={<Register />} />
    <Route path="/oauth/callback" element={<OAuthCallback />} />
    <Route
      path="/"
      element={
        <ProtectedRoute>
          <AppLayout />
        </ProtectedRoute>
      }
    >
      <Route index element={<Navigate to="/skills" replace />} />
      <Route path="skills" element={<Skills />} />
      <Route path="skills/create" element={<CreateSkill />} />
      <Route path="skills/:id" element={<SkillDetails />} />
      <Route path="skills/:id/edit" element={<EditSkill />} />
      <Route path="settings" element={<UserSettings />} />
    </Route>
  </>
);

export const router = withFaroRouterInstrumentation(createBrowserRouter(routes));
