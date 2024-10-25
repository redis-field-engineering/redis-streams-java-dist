const defaultTheme = require('tailwindcss/defaultTheme');
const plugin = require('tailwindcss/plugin');

module.exports = {
  content: ["./_*/*.{html,js}", "./*.html", "./src/**/*.{html,js,jsx,ts,tsx}'"],
  theme: {
    extend: {
      boxShadow: {
        'md': '0 4px 6px -1px hsla(198, 23%, 3%, 0.06), 0 2px 4px -1px hsla(198, 23%, 3%, 0.06)',
        'lg': '0 10px 15px -3px hsla(198, 23%, 3%, 0.06), 0 4px 6px -2px hsla(198, 23%, 3%, 0.12)'
      },
      fontSize: {
        'xs': '.75rem',
        'sm': '.875rem',
        'tiny': '.875rem',
        'base': '1rem',
        'lg': '1.125rem',
        'xl': '1.25rem',
        '2xl': '1.5rem',
        '3xl': '1.875rem',
        '4xl': '2.25rem',
        '5xl': '3rem',
        '6xl': '3.5rem',
        '7xl': '4rem'
      },
      fontFamily: {
        sans: [ 'Space Grotesk', ...defaultTheme.fontFamily.sans ],
        mono: [ 'Space Mono', 'SF Mono', ...defaultTheme.fontFamily.mono ],
        trailers: [ 'TT Trailers', ...defaultTheme.fontFamily.sans ],
      },
      colors: {
        gray: {
          '1\/2': 'hsl(201, 15%, 97%)',
          '01': 'hsl(201, 15%, 93%)',
          '02': 'hsl(201, 15%, 86%)',
          '03': 'hsl(201, 17%, 77%)',
          '04': 'hsl(200, 17%, 67%)',
          '05': 'hsl(200, 17%, 58%)',
          '06': 'hsl(200, 17%, 53%)',
          '07': 'hsl(199, 17%, 44%)',
          '08': 'hsl(199, 17%, 36%)',
          '09': 'hsl(199, 18%, 28%)',
          '10': 'hsl(198, 18%, 21%)',
          '11': 'hsl(198, 18%, 12%)',
          '12': 'hsl(198, 20%, 3%)'
        },
        red: {
          '01': 'hsl(7, 52%, 93%)',
          '02': 'hsl(10, 73%, 86%)',
          '03': 'hsl(10, 72%, 77%)',
          '04': 'hsl(10, 75%, 67%)',
          '05': 'hsl(8, 78%, 58%)',
          '06': 'hsl(3, 76%, 53%)',
          '07': 'hsl(360, 63%, 44%)',
          '08': 'hsl(2, 56%, 36%)',
          '09': 'hsl(3, 51%, 28%)',
          '10': 'hsl(4, 47%, 21%)',
          '11': 'hsl(4, 43%, 12%)',
          '12': 'hsl(359, 45%, 3%)'
        },
        orange: {
          '01': 'hsl(22, 58%, 92%)',
          '02': 'hsl(24, 67%, 83%)',
          '03': 'hsl(23, 76%, 73%)',
          '04': 'hsl(24, 79%, 60%)',
          '05': 'hsl(21, 82%, 49%)',
          '06': 'hsl(15, 74%, 45%)',
          '07': 'hsl(15, 66%, 39%)',
          '08': 'hsl(16, 59%, 31%)',
          '09': 'hsl(15, 54%, 25%)',
          '10': 'hsl(13, 48%, 18%)',
          '11': 'hsl(15, 45%, 11%)',
          '12': 'hsl(22, 52%, 2%)'
        },
        yellow: {
          '01': 'hsl(48, 79%, 88%)',
          '02': 'hsl(47, 80%, 76%)',
          '03': 'hsl(47, 70%, 58%)',
          '04': 'hsl(47, 77%, 44%)',
          '05': 'hsl(47, 69%, 38%)',
          '06': 'hsl(47, 59%, 34%)',
          '07': 'hsl(46, 53%, 29%)',
          '08': 'hsl(47, 46%, 25%)',
          '09': 'hsl(45, 41%, 20%)',
          '10': 'hsl(45, 35%, 15%)',
          '11': 'hsl(41, 30%, 10%)',
          '12': 'hsl(28, 23%, 2%)'
        },
        green: {
          '01': 'hsl(142, 50%, 89%)',
          '02': 'hsl(145, 42%, 78%)',
          '03': 'hsl(143, 35%, 67%)',
          '04': 'hsl(141, 31%, 56%)',
          '05': 'hsl(139, 34%, 45%)',
          '06': 'hsl(123, 34%, 39%)',
          '07': 'hsl(125, 35%, 32%)',
          '08': 'hsl(132, 35%, 26%)',
          '09': 'hsl(136, 38%, 20%)',
          '10': 'hsl(138, 30%, 15%)',
          '11': 'hsl(139, 27%, 10%)',
          '12': 'hsl(127, 27%, 2%)'
        },
        cyan: {
          '01': 'hsl(186, 65%, 89%)',
          '02': 'hsl(186, 61%, 78%)',
          '03': 'hsl(184, 52%, 65%)',
          '04': 'hsl(185, 43%, 53%)',
          '05': 'hsl(185, 48%, 43%)',
          '06': 'hsl(185, 47%, 37%)',
          '07': 'hsl(185, 50%, 31%)',
          '08': 'hsl(185, 49%, 24%)',
          '09': 'hsl(185, 47%, 19%)',
          '10': 'hsl(186, 40%, 15%)',
          '11': 'hsl(183, 35%, 9%)',
          '12': 'hsl(198, 33%, 2%)'
        },
        blue: {
          '01': 'hsl(206, 71%, 92%)',
          '02': 'hsl(208, 91%, 84%)',
          '03': 'hsl(209, 92%, 75%)',
          '04': 'hsl(212, 87%, 67%)',
          '05': 'hsl(215, 90%, 60%)',
          '06': 'hsl(222, 96%, 57%)',
          '07': 'hsl(219, 68%, 45%)',
          '08': 'hsl(218, 60%, 36%)',
          '09': 'hsl(219, 50%, 28%)',
          '10': 'hsl(219, 45%, 20%)',
          '11': 'hsl(219, 40%, 14%)',
          '12': 'hsl(208, 39%, 2%)'
        },
        violet: {
          '1\/2': 'hsl(242, 61%, 97%)',
          '01': 'hsl(250, 25%, 93%)',
          '02': 'hsl(245, 35%, 87%)',
          '03': 'hsl(247, 42%, 80%)',
          '04': 'hsl(249, 52%, 74%)',
          '05': 'hsl(252, 68%, 69%)',
          '06': 'hsl(257, 93%, 64%)',
          '07': 'hsl(258, 96%, 60%)',
          '08': 'hsl(255, 61%, 47%)',
          '09': 'hsl(252, 47%, 35%)',
          '10': 'hsl(249, 40%, 25%)',
          '11': 'hsl(220, 34%, 12%)',
          '12': 'hsl(242, 31%, 3%)'
        },
        magenta: {
          '01': 'hsl(307, 45%, 94%)',
          '02': 'hsl(306, 62%, 86%)',
          '03': 'hsl(302, 69%, 78%)',
          '04': 'hsl(295, 86%, 67%)',
          '05': 'hsl(298, 81%, 59%)',
          '06': 'hsl(301, 52%, 48%)',
          '07': 'hsl(302, 43%, 41%)',
          '08': 'hsl(304, 37%, 34%)',
          '09': 'hsl(305, 32%, 27%)',
          '10': 'hsl(305, 29%, 18%)',
          '11': 'hsl(310, 25%, 12%)',
          '12': 'hsl(275, 19%, 2%)'
        },
        midnight: {
					700: '#161F31',
				},
				neutral: {
					150: '#F0F0F0'
				},
				'redis-red': {
					500: '#FF4438',
					600: '#D52D1F'
				},
				'redis-yellow': {
					100: '#FBFFE8',
					300: '#EDFF8E',
					500: '#DCFF1E',
				},
        'redis-skyblue': {
          100: '#F2FBFF',
          300: '#BFEDFF',
          500: '#80DBFF',
        },
        'redis-purple': {
          100: '#F9F4FC',
          300: '#E3CAF1',
          500: '#C795E3',
        },
				'redis-indigo': {
					500: '#5961ff',
					600: '#454CD5'
				},
				'redis-pen': {
					200: '#E8EBEC',
					300: '#B9C2C6',
					400: '#8A99A0',
					600: '#5C707A',
					700: '#2D4754',
					800: '#163341'
				},
				'redis-pencil': {
					200: '#E5E5E5',

					250: '#D9D9D9',

					300: '#B2B2B2',
					500: '#808080',
					600: '#4C4C4C',

					700: '#444444',

					900: '#191919',
					950: '#000000'
				},
				'redis-ink': {
					900: '#091A23'
				},
				'redis-neutral': {
					800: '#4E545B'
				},
        // 'dark-blue': '#1E293B', // Dark slate blue
        // 'blue-accent': '#3B82F6', // Primary accent blue
        // 'blue-accent-dark': '#1D4ED8', // Darker accent blue
        // 'blue-dark': '#0F172A', // Even darker for borders or accents
        // 'dark-gray': '#111827', // Background gray
        // 'light-gray': '#F1F5F9', // Light gray text
      },
	  typography: (theme) => (  {
        DEFAULT: {
          css: {
            color: theme('colors.redis-ink.900'),
            a: {
              transition: '.2s all',
			  			textDecoration: 'underline',
							fontWeight: '400',
            },
            code: {
              fontWeight: '500',
            },
            pre: {
              padding: '1.25rem',
            },
          },
        },
        lg: {
          css: {
            lineHeight: '1.6',
          },
        },
      }),
    },
    aspectRatio: {
      none: 0,
      square: [1, 1],
      '16/9': [16, 9],
      '4/3': [4, 3],
      '21/9': [21, 9]
    }
  },
  variants: {
    aspectRatio: ['responsive']
  },
  plugins: [
    require('@tailwindcss/typography'),
    plugin(function({ addComponents, theme }) {
			const buttons = {
				'.button-xs, .button-sm, .button, .button-lg, .button-xl, .button-2xl': {
					display: 'inline-flex',
					alignItems: 'center',
					justifyContent: 'center',
					fontWeight: '600',
					letterSpacing: theme('letterSpacing.normal'),
					borderRadius: theme('borderRadius.sm'),
					whiteSpace: 'nowrap',
					transition: '.2s all',
				},
				'.button-xs': {
					fontSize: theme('fontSize.sm'),
					paddingLeft: theme('spacing.1'),
					paddingRight: theme('spacing.1'),
					height: '30px'
				},
				'.button-sm': {
					fontSize: theme('fontSize.sm'),
					paddingLeft: theme('spacing.3'),
					paddingRight: theme('spacing.3'),
					height: '34px'
				},
				'.button': {
					fontSize: theme('fontSize.sm'),
					paddingLeft: theme('spacing.3'),
					paddingRight: theme('spacing.3'),
					height: '38px'
				},
				'.button-lg': {
					fontSize: theme('fontSize.sm'),
					paddingLeft: theme('spacing.4'),
					paddingRight: theme('spacing.4'),
					height: '42px'
				},
				'.button-xl': {
					fontSize: theme('fontSize.base'),
					paddingLeft: theme('spacing.5'),
					paddingRight: theme('spacing.5'),
					height: '48px'
				},
				'.button-2xl': {
					fontSize: theme('fontSize.lg'),
					paddingLeft: theme('spacing.6'),
					paddingRight: theme('spacing.6'),
					height: '60px'
				}
      };
      addComponents([ buttons ]);
		})
  ],
}