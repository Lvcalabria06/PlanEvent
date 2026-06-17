# PlanEvent — sobe banco (Docker), backend e frontend para desenvolvimento local.
# Pré-requisitos: Docker Desktop com engine ativo, WSL2 instalado, .env na raiz do projeto.

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $Root

$env:Path = [System.Environment]::GetEnvironmentVariable("Path", "Machine") + ";" +
            [System.Environment]::GetEnvironmentVariable("Path", "User")

Write-Host "==> Verificando Docker..." -ForegroundColor Cyan
docker info | Out-Null
if ($LASTEXITCODE -ne 0) {
    Write-Host "Docker nao esta pronto. Abra o Docker Desktop e aguarde 'Engine running'." -ForegroundColor Red
    Write-Host "Se acabou de instalar WSL, reinicie o Windows antes." -ForegroundColor Yellow
    exit 1
}

Write-Host "==> Subindo PostgreSQL (planevent-db)..." -ForegroundColor Cyan
docker compose up -d planevent-db

Write-Host "==> Aguardando porta 5432..." -ForegroundColor Cyan
$ready = $false
for ($i = 1; $i -le 30; $i++) {
    if ((Test-NetConnection -ComputerName localhost -Port 5432 -WarningAction SilentlyContinue).TcpTestSucceeded) {
        $ready = $true
        break
    }
    Start-Sleep -Seconds 2
}
if (-not $ready) {
    Write-Host "PostgreSQL nao respondeu na porta 5432." -ForegroundColor Red
    exit 1
}

Write-Host "==> Iniciando backend (porta 3000) em nova janela..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$Root'; .\mvnw.cmd spring-boot:run -pl presentation-backend"

Write-Host "==> Iniciando frontend (porta 8080) em nova janela..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$Root\presentation-frontend'; npm run dev"

Write-Host ""
Write-Host "Pronto! Acesse http://localhost:8080" -ForegroundColor Green
Write-Host "Backend API: http://localhost:3000/api/eventos" -ForegroundColor Green
