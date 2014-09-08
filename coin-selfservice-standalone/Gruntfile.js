module.exports = function(grunt) {
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    now: Date.now(),
    concat: {
      js: {
        files: {
          'dist/application-<%= now %>.js': [
            'src/javascripts/lib/react-with-addons.js',
            'tmp/init.js',
            'tmp/**/*.js',
            'src/javascripts/*',
            '!src/javascripts/*.jsx'
          ]
        }
      }
    },
    uglify: {
      js: {
        files: {
          'dist/application-<%= now %>.min.js': 'dist/application-<%= now %>.js'
        }
      }
    },
    sass: {
      options: {
        compass: true,
        require: 'sass-globbing'
      },
      dev: {
        options: {
          style: 'expanded',
          lineNumbers: true,
          trace: true
        },
        files: {
          'dist/application-<%= now %>.css': 'src/stylesheets/application.sass'
        }
      },
      dist: {
        options: {
          style: 'compressed'
        },
        files: {
          'dist/application-<%= now %>.min.css': 'src/stylesheets/application.sass'
        }
      }
    },
    react: {
      dynamic_mappings: {
        files: [{
          expand: true,
          cwd: 'src/javascripts',
          src: ['init.jsx', '**/*.jsx'],
          dest: 'tmp',
          ext: '.js'
        }]
      }
    },
    watch: {
      files: ['src/**/*', 'Gruntfile.js'],
      tasks: ['default'],
      options: {
        atBegin: true
      }
    },
    clean: {
      tmp: ['tmp/*'],
      dist: ['dist/*']
    },
    'string-replace': {
      dev: {
        files: {
          'dist/index.html': 'src/index.html'
        },
        options: {
          replacements: [{
            pattern: '@@@JS@@@',
            replacement: 'application-<%= now %>.js'
          },
          {
            pattern: '@@@CSS@@@',
            replacement: 'application-<%= now %>.css'
          }]
        }
      },
      dist: {
        files: {
          'dist/index.html': 'src/index.html'
        },
        options: {
          replacements: [{
            pattern: '@@@JS@@@',
            replacement: 'application-<%= now %>.min.js'
          },
          {
            pattern: '@@@CSS@@@',
            replacement: 'application-<%= now %>.min.css'
          }]
        }
      }
    },
    connect: {
      dev: {
        options: {
          base: "dist",
          keepalive: true,
          logger: "dev",
          middleware: function (connect, options, middlewares) {
            var proxy = require('grunt-connect-proxy/lib/utils').proxyRequest;
            return [proxy, connect.static(options.base[0]), connect.directory(options.base[0])];
          }
        },
        proxies: [
          {
            context: ['/selfservice'],
            host: 'localhost',
            port: 8280,
            https: false,
            changeOrigin: false,
            xforward: false
          }
        ]
      }
    }
  });

  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-contrib-watch');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-sass');
  grunt.loadNpmTasks('grunt-string-replace');
  grunt.loadNpmTasks('grunt-contrib-clean');
  grunt.loadNpmTasks('grunt-contrib-connect');
  grunt.loadNpmTasks('grunt-react');
  grunt.loadNpmTasks('grunt-connect-proxy');

  grunt.registerTask('server', [ 'configureProxies:dev', 'connect:dev']);

  grunt.registerTask('prod', ['clean', 'react', 'sass:dist', 'concat:js', 'uglify:js', 'string-replace:dist']);

  grunt.registerTask('default', ['clean', 'react', 'sass:dev', 'concat:js', 'string-replace:dev']);
};
