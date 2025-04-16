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
        breadcrumbs: false},
        {
        id: 'main_dashbaord',
        title: 'Main Dashboard',
        type: 'item',
        url: 'admin/pay_dashboard',
        target: true,
        breadcrumbs: false
      },
      {
        id: 'Subscription',
        title: 'Subscription',
        type: 'collapse',
        icon: 'ti ti-key',
        children: [
          {//to change
            id: 'subscription_list',
            title: 'Subscription List',
            type: 'item',
            url: '/Sub/list',
            target: true,
            breadcrumbs: false
          },
          {
            id: '******',
            title: '*****',
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
          {//to change
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
            url: '/Payment/list',
            target: true,
            breadcrumbs: false
          }
        ]
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
      },
      {
        id: 'quiz',
        title: 'Quiz',
        type: 'collapse', // Make it collapsible
        icon: 'ti ti-book', // Replace with an appropriate icon
        children: [
          {
            id: 'quiz-list',
            title: 'Quiz List',
            type: 'item',
            url: '/quiz/list', // URL for the quiz list page
            classes: 'nav-item',
            breadcrumbs: true
          },
          {
            id: 'create-quiz',
            title: 'Create Quiz',
            type: 'item',
            url: '/quizzes', // URL for the create quiz page
            classes: 'nav-item',
            breadcrumbs: true
          }
        ]
      }
    ]
  },
  {
    id: 'elements',
    title: 'Elements',
    type: 'group',
    icon: 'icon-navigation',
    children: [
      {
        id: 'typography',
        title: 'Typography',
        type: 'item',
        classes: 'nav-item',
        url: '/typography',
        icon: 'ti ti-typography'
      },
      {
        id: 'color',
        title: 'Colors',
        type: 'item',
        classes: 'nav-item',
        url: '/color',
        icon: 'ti ti-brush'
      },
      {
        id: 'tabler',
        title: 'Tabler',
        type: 'item',
        classes: 'nav-item',
        url: 'https://tabler-icons.io/',
        icon: 'ti ti-plant-2',
        target: true,
        external: true
      }
    ]
  },
  {
    id: 'other',
    title: 'Other',
    type: 'group',
    icon: 'icon-navigation',
    children: [
      {
        id: 'sample-page',
        title: 'Sample Page',
        type: 'item',
        url: '/sample-page',
        classes: 'nav-item',
        icon: 'ti ti-brand-chrome'
      },
      {
        id: 'document',
        title: 'Document',
        type: 'item',
        classes: 'nav-item',
        url: 'https://codedthemes.gitbook.io/berry-angular/',
        icon: 'ti ti-vocabulary',
        target: true,
        external: true
      }
    ]
  }
];