import { Card, CardContent } from "./ui/card";

export const Permission=()=>{
 return (
          <div className="space-y-6">
            <h1 className="text-3xl font-bold">Permission Denied</h1>
            <Card>
              <CardContent className="p-6">
                <p className="text-muted-foreground">
                  You do not have permission to view this page.
                </p>
              </CardContent>
            </Card>
          </div>
        );
}