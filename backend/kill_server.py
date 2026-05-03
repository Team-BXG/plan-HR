import subprocess
try:
    output = subprocess.check_output('wmic process where "commandline like \'%manage.py runserver%\'" get processid', shell=True).decode()
    for line in output.split():
        line = line.strip()
        if line.isdigit():
            print(f"Killing pid {line}")
            subprocess.call(['taskkill', '/F', '/PID', line])
except Exception as e:
    print(e)
