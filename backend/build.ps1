$env:JAVA_HOME = "C:\Users\Admin\.jdks\openjdk-22.0.1"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
Set-Location "C:\Projekt Studia\backend"
& .\mvnw.cmd compile -DskipTests