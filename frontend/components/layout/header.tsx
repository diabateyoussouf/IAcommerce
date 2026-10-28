import MobileMenu from "./mobile-menu"
import RightActions from "../shared/right-actions"
import LaptopMenu from "./laptop-menu"
import axios from "axios"
import { API_BASE_URL } from '@/lib/utils';

export default async function Header() {
  let categories: any[] = [];

  try {
    const response = await axios.get(`${API_BASE_URL}/api/categories/images`);
    const data = response.data;

    // Extraction sécurisée selon le format renvoyé par Spring Boot
    if (Array.isArray(data)) {
      categories = data;
    } else if (data && Array.isArray(data.content)) {
      categories = data.content;
    }
  } catch (e) {
    categories = [];
  }

  return (
    <header className="border-b px-4 md:px-6 sticky top-2 rounded-md z-30 bg-background">
      <div className="flex h-16 items-center justify-between gap-4">
        <div className="flex items-center gap-2">
          <MobileMenu />
          <LaptopMenu categories={categories} />
        </div>
        <RightActions />
      </div>
    </header>
  )
}