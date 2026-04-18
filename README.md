# Medixtractor

## Quick Start

### Launch Frontend

```bash
cd medixtractor-front
npm install
npm run dev
```

### Launch Backend

From repo root (recommended):
```bash
./gradlew bootRun
```

Or from the backend folder:
```bash
cd medixtractor-back
./gradlew bootRun
```

### Import BDPM (online)

Backend endpoint: `POST /api/imports/bdpm/remote` (downloads official BDPM files and imports them).
