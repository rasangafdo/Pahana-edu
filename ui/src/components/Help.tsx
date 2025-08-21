import { Card, CardContent } from "./ui/card";

export const Help=()=>{
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
}