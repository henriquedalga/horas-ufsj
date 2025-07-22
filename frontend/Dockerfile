# --- Estágio 1: Build da Aplicação React ---
# Usamos uma imagem oficial do Node.js. A tag 'alpine' resulta em uma imagem menor.
FROM node:20-alpine AS builder

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia os arquivos de dependência primeiro para aproveitar o cache do Docker
COPY package.json package-lock.json ./

# Instala as dependências do projeto
RUN npm install

# Copia o resto do código da sua aplicação para o container
COPY . .

# Roda o comando de build do Vite, que gera a pasta /dist
RUN npm run build


# --- Estágio 2: Servidor de Produção com Nginx ---
# Começamos de uma imagem nova e super leve do Nginx
FROM nginx:stable-alpine

# Copia os arquivos estáticos construídos no estágio anterior (da pasta /app/dist do 'builder')
# para a pasta padrão que o Nginx usa para servir conteúdo estático.
COPY --from=builder /app/dist /usr/share/nginx/html

# Copia o arquivo de configuração customizado do Nginx (ver Passo 2)
# para corrigir o roteamento de Single-Page Applications (SPA).
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expõe a porta 80, que é a porta padrão do Nginx
EXPOSE 80

# O comando para iniciar o Nginx já é o padrão da imagem, então não precisamos de um CMD.