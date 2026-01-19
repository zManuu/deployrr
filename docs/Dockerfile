FROM node:current-alpine
COPY . /docs
RUN npm install -g docsify-cli@latest
EXPOSE 3000/tcp
ENTRYPOINT exec docsify serve /docs