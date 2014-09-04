module.exports = function(grunt) {
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    now: Date.now(),
    concat: {
      js: {
        files: {
          'dist/application-<%= now %>.js': [
            'src/javascripts/lib/react-with-addons.js',
            'tmp/*.js',
            'src/javascripts/*',
            '!src/javascripts/*.jsx',
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
      dev: {
        options: {
          style: 'expanded',
          lineNumbers: true
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
          src: ['**/*.jsx'],
          dest: 'tmp',
          ext: '.js'
        }]
      }
    },
    watch: {
      files: ['src/**/*'],
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
          keepalive: true
        }
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
  grunt.registerTask('prod', ['clean', 'react', 'sass:dist', 'concat:js', 'uglify:js', 'string-replace:dist']);

  grunt.registerTask('default', ['clean', 'react', 'sass:dev', 'concat:js', 'string-replace:dev']);
};
