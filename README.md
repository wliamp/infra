# 🔧 Infrastructure Components

Core infrastructure components support scalable microservices. Provide reusable foundations for common functionalities, enabling faster development and consistent architecture across projects.

## 📁 Spring Boot Projects

- [API Gateway](./api-gateway) – Acts as a secure gateway for client requests, responsible for routing, authenticating JWT tokens, and forwarding validated requests to internal APIs.

- [Config Server](./config-server) – Centralized configuration management module responsible for serving externalized configuration properties to all microservices, supporting dynamic property updates and environment-specific profiles.

- [Identity Provider](./identity-provider) – Responsible for handling user identity and authentication across the system, supporting multiple login methods). It issues and validates JWT tokens, acting as the central authority for identity management and secure communication between clients and microservices.

- [Service Registry](./service-registry) – The central service registry module responsible for registering and managing all microservices within the distributed system.

Each module is independently runnable and documented in its own README.md file.

---

### 🛠 Contribute

If you’d like to contribute or share your code, **do not commit directly to 'main'**

Please create a new branch using the allowed proper prefixes: **feature/** , **bugfix/** , **hotfix/** , **dev/** , **release/**

#### ⚠️ If you don’t use a proper prefix, your branch might not be protected and could be modified by others

Then open a Pull Request (PR) to merge into 'main'

#### ➡️ All changes will be reviewed before merging, use meaningful branch names and commit messages

---

### ⚖️ License:
This repository is licensed under the **MIT** License

---

### 🧑‍💻 Author:
[William Phan](https://github.com/wliamp)

---

### 📫 Contact:
`phnam230197@gmail.com`
