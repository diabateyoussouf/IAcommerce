import type { NextConfig } from "next";
import createNextIntlPlugin from 'next-intl/plugin';

// Récupère l'URL du backend depuis .env.local (avec fallback local sur port 8005)
const apiUrl = process.env.NEXT_PUBLIC_API_BASE_URL ;

// Base CSP directives
const ContentSecurityPolicy = {
  "default-src": "'self'",
  "script-src": [
    "'self'",
    "'unsafe-inline'", 
  ],
  "object-src": "'none'",
  "style-src": "'self' 'unsafe-inline'", 
  "img-src": "'self' data: blob: https: http:", 
  "font-src": "'self' https:",
  "connect-src": `'self' ${apiUrl}`,
  "frame-ancestors": "'none'",
};

if (process.env.NODE_ENV === 'development') {
  ContentSecurityPolicy['script-src'].push("'unsafe-eval'");
}

const cspValue = Object.entries(ContentSecurityPolicy)
  .map(([key, value]) => `${key} ${Array.isArray(value) ? value.join(' ') : value}`)
  .join('; ');

const securityHeaders = [
  {
    key: 'Content-Security-Policy',
    value: cspValue,
  },
  {
    key: 'Strict-Transport-Security',
    value: 'max-age=63072000; includeSubDomains; preload',
  },
  {
    key: 'X-Frame-Options',
    value: 'DENY',
  },
  {
    key: 'X-Content-Type-Options',
    value: 'nosniff',
  },
  {
    key: 'Referrer-Policy',
    value: 'strict-origin-when-cross-origin',
  },
  {
    key: 'Permissions-Policy',
    value: 'geolocation=(), microphone=(), camera=()',
  },
];

const nextConfig: NextConfig = {
  experimental: {
    serverActions: {
      bodySizeLimit: '10mb',
    },
  },

  productionBrowserSourceMaps: true,

  images: {
    unoptimized: true, // Contourne le proxy serveur pour éviter les erreurs 400/500 sur les images externes
    dangerouslyAllowSVG: true,
    contentSecurityPolicy: "default-src 'self'; script-src 'none'; sandbox;",
    remotePatterns: [
      { protocol: 'https', hostname: '**' },
      { protocol: 'http', hostname: '**' },
    ],
  },

  async headers() {
    return [
      {
        source: '/:path*',
        headers: securityHeaders,
      },
    ];
  },
};

const withNextIntl = createNextIntlPlugin();
export default withNextIntl(nextConfig);