param(
  [switch]$NewWindows = $true
)

$ErrorActionPreference = "Stop"

$services = @(
  @{ Name = "usuarios";      Path = "microservicios-usuarios-biblioteca";      Port = 8081 },
  @{ Name = "catalogo";      Path = "microservicios-catalogo-biblioteca";      Port = 8082 },
  @{ Name = "circulacion";   Path = "microservicios-circulacion-biblioteca";   Port = 8083 },
  @{ Name = "notificacion";  Path = "microservicios-notificacion-bilbioteca"; Port = 8084 }
)

$pids = @()

foreach ($svc in $services) {
  $fullPath = Join-Path -Path $PSScriptRoot -ChildPath $svc.Path
  if (-not (Test-Path -LiteralPath $fullPath)) {
    throw "No existe la carpeta: $fullPath"
  }

  $cmd = ".\\mvnw.cmd -q spring-boot:run"

  if ($NewWindows) {
    $proc = Start-Process -FilePath "powershell.exe" -ArgumentList @(
      "-NoProfile",
      "-NoExit",
      "-Command",
      $cmd
    ) -WorkingDirectory $fullPath -PassThru
    $pids += @{ name = $svc.Name; port = $svc.Port; pid = $proc.Id }
  } else {
    $proc = Start-Process -FilePath "powershell.exe" -ArgumentList @(
      "-NoProfile",
      "-Command",
      $cmd
    ) -WorkingDirectory $fullPath -PassThru
    $pids += @{ name = $svc.Name; port = $svc.Port; pid = $proc.Id }
  }
}

$pidFile = Join-Path -Path $PSScriptRoot -ChildPath ".microservicios.pids.json"
$pids | ConvertTo-Json | Set-Content -LiteralPath $pidFile -Encoding UTF8

Write-Host "Listo. PIDs guardados en: $pidFile"
Write-Host "Puertos: usuarios 8081, catalogo 8082, circulacion 8083, notificacion 8084"

