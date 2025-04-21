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
          },
        ],
      },
      {
        id: 'courses',
        title: 'Course Management',
        type: 'group',
        icon: 'icon-navigation',
        children: [
          {
            id: 'course-management',
            title: 'Courses',
            type: 'item',
            classes: 'nav-item',
            url: '/courses',
            icon: 'ti ti-book',
            breadcrumbs: false
          },
          {
            id: 'category-management',
            title: 'Categories',
            type: 'item',
            classes: 'nav-item',
            url: '/categories',
            icon: 'ti ti-tag',
            breadcrumbs: false
          },
        ]
      }
    ]
  },
  {
    id: 'subPaym',
    title: 'Payment & Subscription',
    type: 'group',
    children: [
      {
        id: 'main_dashbaord',
        title: 'Main Dashboard',
        type: 'item',
        classes: 'nav-item',
        url: '/admin/pay_dashboard',
        icon: 'ti ti-dashboard',
        breadcrumbs: false
      },
      {
        id: 'Subscription',
        title: 'Subscription',
        type: 'collapse',
        icon: 'ti ti-key',
        children: [
          {
            id: 'subscription_list',
            title: 'Subscription List',
            type: 'item',
            url: '/subs/list',
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
      },
      {
        id: 'Coupon',
        title: 'Coupon',
        type: 'collapse',
        icon: 'ti ti-key',
        children: [
          {
            id: 'create-coupon',
            title: 'Create Coupon',
            type: 'item',
            url: '/coupon/create-coupon',
            target: true,
            breadcrumbs: false
          },
          {
            id: 'coupon_list',
            title: 'Coupon List',
            type: 'item',
            url: '/coupon/list',
            target: true,
            breadcrumbs: false
          }
        ]
      },
      {
        id: 'Payment',
        title: 'Payment',
        type: 'collapse',
        icon: 'ti ti-key',
        children: [
          {
            id: 'Payment_list',
            title: 'Payment List',
            type: 'item',
            url: '/pay/list',
            target: true,
            breadcrumbs: false
          }
        ]
      }
    ]
  },
  {
    id: 'quiz',
    title: 'Quiz',
    type: 'collapse',
    icon: 'ti ti-book',
    children: [
      {
        id: 'quiz-list',
        title: 'Quiz List',
        type: 'item',
        url: '/quiz/list',
        classes: 'nav-item',
        breadcrumbs: true
      },
      {
        id: 'create-quiz',
        title: 'Create Quiz',
        type: 'item',
        url: '/quizzes',
        classes: 'nav-item',
        breadcrumbs: true
      },
      {
        id: 'trivia-quiz',
        title: 'Create Quiz With AI',
        type: 'item',
        url: '/trivia-quiz',
        classes: 'nav-item',
        breadcrumbs: true
      }
    ]
  },
  {
    id: 'exam-management',
    title: 'Exam Management',
    type: 'group',
    icon: 'icon-navigation',
    children: [
      {
        id: 'exam-list',
        title: 'Exams',
        type: 'item',
        url: '/exams',
        icon: 'ti ti-book',
        classes: 'nav-item',
        breadcrumbs: true
      },
      {
        id: 'exam-create',
        title: 'Create Exam',
        type: 'item',
        url: '/exams/create',
        icon: 'ti ti-file-plus',
        classes: 'nav-item',
        breadcrumbs: true
      },
      {
        id: 'exam-grade',
        title: 'Grade Exam',
        type: 'item',
        url: '/exams/grade/:id',
        icon: 'ti ti-pencil',
        classes: 'nav-item',
        breadcrumbs: true
      }
    ]
  }
];