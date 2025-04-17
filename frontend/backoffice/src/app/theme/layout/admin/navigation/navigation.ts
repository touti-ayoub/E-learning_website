export interface NavigationItem {
  id: string;
  title: string;
  type: 'item' | 'collapse' | 'group';
  translate?: string;
  icon?: string;
  hidden?: boolean;
  url?: string;
  classes?: string;
  external?: boolean;
  target?: boolean;
  breadcrumbs?: boolean;
  children?: NavigationItem[];
  role?: string[];
  isMainParent?: boolean;
}

export const NavigationItems: NavigationItem[] = [
  {
    id: 'dashboard',
    title: 'Dashboard',
    type: 'group',
    icon: 'icon-navigation',
    children: [
      {
        id: 'default',
        title: 'Dashboard',
        type: 'item',
        classes: 'nav-item',
        url: '/default',
        icon: 'ti ti-dashboard',
        breadcrumbs: false
      }
    ]
  },
  {
    id: 'events-management',
    title: 'Events Management',
    type: 'group',
    icon: 'icon-navigation',
    children: [
      {
        id: 'events',
        title: 'Events',
        type: 'item',
        classes: 'nav-item',
        url: '/events',
        icon: 'ti ti-calendar-event'
      },
      {
        id: 'feedbacks',
        title: 'Feedbacks',
        type: 'item',
        classes: 'nav-item',
        url: '/feedbacks',
        icon: 'ti ti-messages'
      },
      {
        id: 'registrations',
        title: 'Registrations',
        type: 'item',
        classes: 'nav-item',
        url: '/registrations',
        icon: 'ti ti-ticket'
      },
      {
        id: 'calendar',
        title: 'Calendar',
        type: 'item',
        classes: 'nav-item',
        url: '/calendar',
        icon: 'ti ti-calendar'
      }
    ]
  },
  {
    id: 'page',
    title: 'Pages',
    type: 'group',
    icon: 'icon-navigation',
    children: [
      {
        id: 'Authentication',
        title: 'Authentication',
        type: 'collapse',
        icon: 'ti ti-key',
        children: [
          {
            id: 'login',
            title: 'Login',
            type: 'item',
            url: '/guest/login',
            target: true,
            breadcrumbs: false
          },
          {
            id: 'register',
            title: 'Register',
            type: 'item',
            url: '/guest/register',
            target: true,
            breadcrumbs: false
          }
        ]
      }
    ]
  }
];
