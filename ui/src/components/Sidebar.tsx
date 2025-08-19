import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { 
  LayoutDashboard, 
  Users, 
  Package, 
  ShoppingCart, 
  HelpCircle, 
  LogOut,
  Book
} from "lucide-react";

type View = 'dashboard' | 'customers' | 'items' | 'sales' | 'help';

interface SidebarProps {
  currentView: View;
  onViewChange: (view: View) => void;
  onLogout: () => void;
}

const navigationItems = [
  {
    id: 'dashboard' as View,
    label: 'Dashboard',
    icon: LayoutDashboard,
  },
  {
    id: 'customers' as View,
    label: 'Customers',
    icon: Users,
  },
  {
    id: 'items' as View,
    label: 'Items',
    icon: Package,
  },
  {
    id: 'sales' as View,
    label: 'Sales & Billing',
    icon: ShoppingCart,
  },
  {
    id: 'help' as View,
    label: 'Help',
    icon: HelpCircle,
  },
];

export const Sidebar = ({ currentView, onViewChange, onLogout }: SidebarProps) => {
  return (
    <div className="fixed left-0 top-0 h-full w-64 bg-card border-r border-border shadow-medium">
      <div className="flex flex-col h-full">
        {/* Header */}
        <div className="p-6 border-b border-border">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-gradient-to-br from-primary to-success rounded-xl flex items-center justify-center">
              <Book className="h-5 w-5 text-primary-foreground" />
            </div>
            <div>
              <h2 className="font-bold text-foreground">Pahana Edu</h2>
              <p className="text-xs text-muted-foreground">Management System</p>
            </div>
          </div>
        </div>

        {/* Navigation */}
        <nav className="flex-1 p-4 space-y-2">
          {navigationItems.map((item) => (
            <Button
              key={item.id}
              variant={currentView === item.id ? "default" : "ghost"}
              className={cn(
                "w-full justify-start h-12 text-left",
                currentView === item.id 
                  ? "bg-gradient-to-r from-primary to-primary/80 text-primary-foreground shadow-soft" 
                  : "hover:bg-accent/50"
              )}
              onClick={() => onViewChange(item.id)}
            >
              <item.icon className="h-5 w-5 mr-3" />
              {item.label}
            </Button>
          ))}
        </nav>

        {/* Footer */}
        <div className="p-4 border-t border-border">
          <Button
            variant="outline"
            className="w-full justify-start h-12 text-destructive hover:text-destructive hover:bg-destructive/5"
            onClick={onLogout}
          >
            <LogOut className="h-5 w-5 mr-3" />
            Sign Out
          </Button>
        </div>
      </div>
    </div>
  );
};