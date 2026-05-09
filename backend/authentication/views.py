from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import AllowAny
from rest_framework.response import Response
from employees.models import Employee, Role


def _resolve_role(employee):
    try:
        role_record = Role.objects.get(employee_id=employee.id)
        return role_record.role
    except Role.DoesNotExist:
        if (employee.position or "").lower() == "admin":
            return "Admin"
        if "hr" in (employee.position or "").lower():
            return "HR Manager"
        if "manager" in (employee.position or "").lower():
            return "Manager"
        return "Employee"


@api_view(["POST"])
@permission_classes([AllowAny])
def login_view(request):
    username = (request.data.get("username") or "").strip()
    password = (request.data.get("password") or "").strip()

    if not username or not password:
        return Response({"error": "Username and password are required."}, status=400)

    try:
        employee = Employee.objects.get(id=username)
    except Employee.DoesNotExist:
        return Response({"error": "Invalid ID."}, status=404)

    if not employee.password or employee.password != password:
        return Response({"error": "Invalid password."}, status=400)

    role = _resolve_role(employee)
    return Response(
        {
            "token": f"raw-auth-{employee.id}",
            "user": {
                "id": employee.id,
                "employee_id": employee.id,
                "username": employee.id,
                "name": employee.name,
                "role": role,
                "department": employee.department,
                "position": employee.position,
            },
            "role": role,
        },
        status=200,
    )

@api_view(["POST"])
@permission_classes([AllowAny])
def change_password_view(request):
    username = request.data.get("username")
    old_password = request.data.get("old_password")
    new_password = request.data.get("new_password")
    
    if not username or not old_password or not new_password:
        return Response({"error": "Missing fields"}, status=400)
        
    try:
        employee = Employee.objects.get(id=username)
    except Employee.DoesNotExist:
        return Response({"error": "User not found"}, status=404)
        
    if employee.password != old_password:
        return Response({"error": "Incorrect old password"}, status=400)
        
    employee.password = new_password
    employee.save(update_fields=['password'])
    return Response({"message": "Password updated successfully"}, status=200)
