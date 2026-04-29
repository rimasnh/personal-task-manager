# Personal Task Manager

Small Java 17 Spring Boot task manager with:

- CRUD REST API for tasks
- H2 in-memory database
- AI-powered `POST /tasks/suggest` endpoint
- Minimal frontend at `/`
- Unit and integration tests

## Run

```bash
mvn spring-boot:run
```

The app will be available at `http://localhost:8080`.

## AI endpoint

The application uses OpenAI's chat completions API when `OPENAI_API_KEY` is set.

```bash
export OPENAI_API_KEY=your-key
export OPENAI_MODEL=gpt-4.1-mini
```

If no API key is configured, the endpoint still works using a lightweight local fallback so reviewers can exercise the feature without extra setup.

## API

- `POST /tasks`
- `GET /tasks`
- `GET /tasks/{id}`
- `PUT /tasks/{id}`
- `DELETE /tasks/{id}`
- `POST /tasks/suggest`
