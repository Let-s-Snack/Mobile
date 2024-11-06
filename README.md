# Let's Snack - Projeto Mobile
## Informações gerais
O projeto mobile foi construido utilizando as linguagens **Java** e **Kotlin** para o desenvolvimento back-end **XML** para a construção de toda parte visual. Também usamos alguns serviços do Firebase, como o **Firebase Authenticator**, na qual estamos usando para fazermos o cadastro/login dos usuários, **Firebase Cloud Storage** para guardarmos as imagens geradas no nosso banco e o **Firebase CloudStorage** para o Chat IA. Toda manipulação e consumo de imagens foram feitos a partir do **Picasso**  e do **Glide**, já o consumo das APIs foram feitas a partir do **Retrofit**

## Funcionalidades
Dentro do aplicativo foram desenvolvidas as seguintes funcionalidades:

- **Cadastro/Login dos usuários**

    - O usuário faz a inserção dos dados e nos salvamos eles no Firebase e no Mongo, consumindo a API NoSQL
    - Durante o processo do cadastro o usuário pode fazer a inserção de uma foto de perfil, e após essa inserção nós salvamos essa foto no Cloud Storage.

- **Home**
    - Nossa home é composta por um feed de receita, em que nos listamos ao usuário algumas categorias de receitas com base em suas restrições.

- **Buscar:**
    - A sessão buscar contém todas as categorias, dando a possibilidade dos usuários fazerem a busca de todas as receitas de cada categoria.
        - Dentro do feed de receitas podemos abrir uma receita, mostrando sua tela com as informações da mesma.
    - Também podemos fazer a busca de uma receita pelo seu nome.

- **Receita**
    - A tela de receita contém todas as informações da mesma, possibilitando também o usuário salver os ingredientes ou curtir/descurtir a receita.

- **Chat IA**
    - O Chat IA é um chat de conversas integrada com a API do GPT-4 restrito para assuntos relacionados a alimentos.

- **Curtidos**
    - A sessão de curtidos é composta pelas receitas que os usuários curtiram durante o fluxo de uso do app.

- **Perfil**
    - Dentro do perfil temos a opção de editar os dados, visualizar os seus ingredientes salvos e ler os termos de uso.
    - Caso o usuário for administrador ele consegue visualizar a área restrita, sendo uma webview com o projeto em React


## Linguagens e Ferramentas:
<p align="left"> <a href="https://developer.android.com" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/android/android-original-wordmark.svg" alt="android" width="40" height="40"/> </a> <a href="https://www.figma.com/" target="_blank" rel="noreferrer"> <img src="https://www.vectorlogo.zone/logos/figma/figma-icon.svg" alt="figma" width="40" height="40"/> </a> <a href="https://firebase.google.com/" target="_blank" rel="noreferrer"> <img src="https://www.vectorlogo.zone/logos/firebase/firebase-icon.svg" alt="firebase" width="40" height="40"/> </a> <a href="https://git-scm.com/" target="_blank" rel="noreferrer"> <img src="https://www.vectorlogo.zone/logos/git-scm/git-scm-icon.svg" alt="git" width="40" height="40"/> </a> <a href="https://www.java.com" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/java/java-original.svg" alt="java" width="40" height="40"/> </a> <a href="https://kotlinlang.org" target="_blank" rel="noreferrer"> <img src="https://www.vectorlogo.zone/logos/kotlinlang/kotlinlang-icon.svg" alt="kotlin" width="40" height="40"/> </a></p>



## Autores
- [@Gustavo Teotônio](https://github.com/Gustavo-Teotonio)
- [@Pedro Schettini](https://github.com/pedroschettini)
- [@Artur Nascimento](https://github.com/arturnascimentosousa)