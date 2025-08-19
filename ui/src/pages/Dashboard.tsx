import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { 
  Users, 
  Package, 
  ShoppingCart, 
  TrendingUp,
  Book,
  LogOut,
  Plus,
  Search,
  Calendar,
  DollarSign
} from "lucide-react";
import { Sidebar } from "@/components/Sidebar";
import { CustomerManagement } from "@/components/CustomerManagement";
import { ItemManagement } from "@/components/ItemManagement";
import { SalesBilling } from "@/components/SalesBilling";

type View = 'dashboard' | 'customers' | 'items' | 'sales' | 'help';

const Dashboard = ({ onLogout }: { onLogout: () => void }) => {
  const [currentView, setCurrentView] = useState<View>('dashboard');

  const stats = [
    {
      title: "Total Customers",
      value: "1,247",
      change: "+12%",
      icon: Users,
      color: "text-primary"
    },
    {
      title: "Items in Stock",
      value: "3,456",
      change: "+8%",
      icon: Package,
      color: "text-success"
    },
    {
      title: "Today's Sales",
      value: "₹24,680",
      change: "+15%",
      icon: DollarSign,
      color: "text-warning"
    },
    {
      title: "Monthly Growth",
      value: "18.5%",
      change: "+3%",
      icon: TrendingUp,
      color: "text-info"
    }
  ];

  const recentSales = [
    { id: "S001", customer: "Radhika Perera", amount: "₹2,450", time: "2 hours ago" },
    { id: "S002", customer: "Nuwan Silva", amount: "₹1,890", time: "4 hours ago" },
    { id: "S003", customer: "Priya Fernando", amount: "₹3,200", time: "6 hours ago" },
  ];

  const renderDashboardContent = () => {
    return (
      <div className="space-y-6">
        {/* Header */}
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-foreground">Dashboard</h1>
            <p className="text-muted-foreground mt-1">Welcome back! Here's what's happening today.</p>
          </div>
          <div className="flex items-center gap-3">
            <Badge variant="secondary" className="px-3 py-1">
              <Calendar className="h-4 w-4 mr-1" />
              {new Date().toLocaleDateString('en-GB', { 
                weekday: 'long', 
                year: 'numeric', 
                month: 'long', 
                day: 'numeric' 
              })}
            </Badge>
          </div>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {stats.map((stat, index) => (
            <Card key={index} className="hover:shadow-medium transition-shadow duration-300">
              <CardContent className="p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm font-medium text-muted-foreground">{stat.title}</p>
                    <p className="text-2xl font-bold text-foreground mt-1">{stat.value}</p>
                    <Badge variant="secondary" className="mt-2 text-xs">
                      {stat.change} from last month
                    </Badge>
                  </div>
                  <div className={`p-3 rounded-xl bg-gradient-to-br from-primary/10 to-primary/5`}>
                    <stat.icon className={`h-6 w-6 ${stat.color}`} />
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>

        {/* Quick Actions & Recent Sales */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Quick Actions */}
          <Card>
            <CardHeader>
              <CardTitle>Quick Actions</CardTitle>
              <CardDescription>Common tasks to get you started</CardDescription>
            </CardHeader>
            <CardContent className="space-y-3">
              <Button 
                onClick={() => setCurrentView('customers')} 
                variant="outline" 
                className="w-full justify-start h-12"
              >
                <Plus className="h-4 w-4 mr-2" />
                Add New Customer
              </Button>
              <Button 
                onClick={() => setCurrentView('sales')} 
                variant="outline" 
                className="w-full justify-start h-12"
              >
                <ShoppingCart className="h-4 w-4 mr-2" />
                Create New Sale
              </Button>
              <Button 
                onClick={() => setCurrentView('items')} 
                variant="outline" 
                className="w-full justify-start h-12"
              >
                <Package className="h-4 w-4 mr-2" />
                Manage Inventory
              </Button>
            </CardContent>
          </Card>

          {/* Recent Sales */}
          <Card>
            <CardHeader>
              <CardTitle>Recent Sales</CardTitle>
              <CardDescription>Latest transactions in your store</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {recentSales.map((sale) => (
                  <div key={sale.id} className="flex items-center justify-between p-3 rounded-lg bg-gradient-to-r from-accent/10 to-secondary/10">
                    <div>
                      <p className="font-medium text-foreground">{sale.customer}</p>
                      <p className="text-sm text-muted-foreground">Sale #{sale.id}</p>
                    </div>
                    <div className="text-right">
                      <p className="font-bold text-success">{sale.amount}</p>
                      <p className="text-xs text-muted-foreground">{sale.time}</p>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    );
  };

  const renderContent = () => {
    switch (currentView) {
      case 'customers':
        return <CustomerManagement />;
      case 'items':
        return <ItemManagement />;
      case 'sales':
        return <SalesBilling />;
      case 'help':
        return (
          <div className="space-y-6">
            <h1 className="text-3xl font-bold">Help & Documentation</h1>
            <Card>
              <CardContent className="p-6">
                <p className="text-muted-foreground">
                  Welcome to Pahana Edu Management System. Use the sidebar to navigate between different sections.
                  For support, contact the system administrator.
                </p>
              </CardContent>
            </Card>
          </div>
        );
      default:
        return renderDashboardContent();
    }
  };

  return (
    <div className="min-h-screen bg-background">
      <div className="flex">
        <Sidebar 
          currentView={currentView} 
          onViewChange={setCurrentView}
          onLogout={onLogout}
        />
        <main className="flex-1 p-6 ml-64">
          {renderContent()}
        </main>
      </div>
    </div>
  );
};

export default Dashboard;