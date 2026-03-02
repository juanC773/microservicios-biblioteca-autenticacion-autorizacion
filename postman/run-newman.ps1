$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$collection = Join-Path $root "biblioteca.newman.collection.json"
$envFile = Join-Path $root "local.environment.json"

Write-Host "Running Newman..."
Write-Host "Collection: $collection"
Write-Host "Environment: $envFile"

npx newman run "$collection" -e "$envFile" -r cli

