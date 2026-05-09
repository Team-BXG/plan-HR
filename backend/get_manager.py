import sqlite3

def get_managers():
    conn = sqlite3.connect('db.sqlite3')
    cursor = conn.cursor()
    cursor.execute("SELECT id, name, password, department, position FROM employees WHERE position LIKE '%manager%'")
    rows = cursor.fetchall()
    print("Found managers:")
    for row in rows:
        print(f"ID: {row[0]}, Name: {row[1]}, Pass: {row[2]}, Dept: {row[3]}, Pos: {row[4]}")
    
    # If no manager found, create one
    if not rows:
        print("Creating a default manager user (M001)")
        cursor.execute("INSERT INTO employees (id, name, password, department, position, is_active) VALUES ('M001', 'Default Manager', 'manager123', 'IT', 'Manager', 1)")
        conn.commit()
        print("Created ID: M001, Pass: manager123")
    
    conn.close()

if __name__ == '__main__':
    get_managers()
