# Construção de Sistemas de Software (CSS)
**Licenciatura em Engenharia Informática 24/25**

## SoccerNow Application

---

## 🧑‍🤝‍🧑 Membros do grupo

### 👤 Luís Santos — `fc58437`
### 👤 Liliana Valente — `fc59846`
### 👤 Denis Bahnari — `fc59878`

---

## 📝 Notas

- Todos os casos de uso identificados como **F1** foram implementados e testados
- A aplicação encontra-se preparada para execução via **Docker**, respeitando os requisitos técnicos definidos
- A documentação técnica com os diagramas UML e o relatório de decisões arquiteturais encontra-se disponível na pasta `docs/`
- Os endpoints REST estão acessíveis e testáveis via **Swagger UI**
- O projeto foi desenvolvido seguindo as melhores práticas de desenvolvimento colaborativo, com commits frequentes, testes  
  e controlo de qualidade de código

## 📦 Como compilar o projeto

### Para compilar o servidor:
```bash
cd SoccerNow
docker-compose up --build
```

### Para compilar o cliente JavaFX:
```bash
cd ..
cd ui-javafx
mvn clean install javafx:run
```