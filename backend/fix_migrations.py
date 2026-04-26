import os
import django
from django.core.management import call_command

def reset_db():
    if os.path.exists('db.sqlite3'):
        os.remove('db.sqlite3')
        print("Removed bad db.sqlite3")
        
    print("Making migrations...")
    call_command('makemigrations', 'authentication', 'employees', 'departments', 'attendance', 'leave')
    
    print("Migrating...")
    call_command('migrate')
    
if __name__ == '__main__':
    os.environ.setdefault("DJANGO_SETTINGS_MODULE", "employee_management.settings")
    django.setup()
    reset_db()
