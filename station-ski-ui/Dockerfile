# Multi-stage build pour Angular 17
FROM node:20-alpine AS builder

WORKDIR /app

# Optimisation du cache Docker des dependances
COPY package*.json ./
RUN npm ci --quiet

COPY . .
RUN npm run build -- --configuration production

# Runtime Nginx ultra-leger
FROM nginx:1.27-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist/station-ski-ui/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
