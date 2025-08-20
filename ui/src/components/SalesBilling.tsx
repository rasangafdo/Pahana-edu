import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { Plus, Minus, ShoppingCart, Receipt, Trash2, User, Phone } from "lucide-react";
import { useToast } from "@/hooks/use-toast";

interface Item {
  itemId: number;
  name: string;
  unitPrice: number;
  stockAvailable: number;
  discount: number;
  qtyToAllowDiscount: number;
  categoryName: string;
}

interface SaleItem {
  item: Item;
  qty: number;
  discountAmount: number;
  itemTotal: number;
}

interface Customer {
  name: string;
  telephone: string;
  address: string;
}

export const SalesBilling = () => {
  const [items] = useState<Item[]>([
    {
      itemId: 1,
      name: "Grade 10 Mathematics Textbook",
      unitPrice: 850.00,
      stockAvailable: 45,
      discount: 10.0,
      qtyToAllowDiscount: 5,
      categoryName: "Textbooks"
    },
    {
      itemId: 2,
      name: "Blue Ballpoint Pen (Pack of 10)",
      unitPrice: 120.00,
      stockAvailable: 200,
      discount: 5.0,
      qtyToAllowDiscount: 20,
      categoryName: "Stationery"
    },
    {
      itemId: 3,
      name: "English Dictionary",
      unitPrice: 1250.00,
      stockAvailable: 25,
      discount: 15.0,
      qtyToAllowDiscount: 3,
      categoryName: "Reference Books"
    },
    {
      itemId: 4,
      name: "A4 Ruled Notebook (200 pages)",
      unitPrice: 180.00,
      stockAvailable: 150,
      discount: 8.0,
      qtyToAllowDiscount: 10,
      categoryName: "Notebooks"
    }
  ]);

  const [customer, setCustomer] = useState<Customer>({
    name: "",
    telephone: "",
    address: ""
  });

  const [saleItems, setSaleItems] = useState<SaleItem[]>([]);
  const [selectedItemId, setSelectedItemId] = useState<string>("");
  const { toast } = useToast();

  const addItemToSale = () => {
    if (!selectedItemId) return;

    const item = items.find(i => i.itemId.toString() === selectedItemId);
    if (!item) return;

    const existingItem = saleItems.find(si => si.item.itemId === item.itemId);
    if (existingItem) {
      updateItemQuantity(item.itemId, existingItem.qty + 1);
    } else {
      const newSaleItem: SaleItem = {
        item,
        qty: 1,
        discountAmount: 0,
        itemTotal: item.unitPrice
      };
      setSaleItems([...saleItems, newSaleItem]);
    }
    setSelectedItemId("");
  };

  const updateItemQuantity = (itemId: number, newQty: number) => {
    if (newQty <= 0) {
      removeItemFromSale(itemId);
      return;
    }

    setSaleItems(saleItems.map(saleItem => {
      if (saleItem.item.itemId === itemId) {
        const hasDiscount = newQty >= saleItem.item.qtyToAllowDiscount;
        const discountAmount = hasDiscount ? (saleItem.item.unitPrice * newQty * saleItem.item.discount / 100) : 0;
        const itemTotal = (saleItem.item.unitPrice * newQty) - discountAmount;
        
        return {
          ...saleItem,
          qty: newQty,
          discountAmount,
          itemTotal
        };
      }
      return saleItem;
    }));
  };

  const removeItemFromSale = (itemId: number) => {
    setSaleItems(saleItems.filter(saleItem => saleItem.item.itemId !== itemId));
  };

  const calculateTotals = () => {
    const subTotal = saleItems.reduce((sum, item) => sum + (item.item.unitPrice * item.qty), 0);
    const totalDiscount = saleItems.reduce((sum, item) => sum + item.discountAmount, 0);
    const totalAmount = subTotal - totalDiscount;
    
    return { subTotal, totalDiscount, totalAmount };
  };

  const handleCreateSale = async () => {
    if (!customer.name || !customer.telephone || !customer.address) {
      toast({
        title: "Error",
        description: "Please fill in all customer details",
        variant: "destructive",
      });
      return;
    }

    if (saleItems.length === 0) {
      toast({
        title: "Error",
        description: "Please add at least one item to the sale",
        variant: "destructive",
      });
      return;
    }

    // Simulate API call
    setTimeout(() => {
      toast({
        title: "Sale Created Successfully",
        description: `Sale completed for ${customer.name}`,
      });
      
      // Reset form
      setCustomer({ name: "", telephone: "", address: "" });
      setSaleItems([]);
    }, 1000);
  };

  const { subTotal, totalDiscount, totalAmount } = calculateTotals();

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-foreground">Sales & Billing</h1>
          <p className="text-muted-foreground mt-1">Create new sales and generate bills</p>
        </div>
        <Badge variant="secondary" className="px-3 py-1">
          <Receipt className="h-4 w-4 mr-1" />
          New Sale
        </Badge>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Left Column - Customer & Items */}
        <div className="space-y-6">
          {/* Customer Information */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <User className="h-5 w-5" />
                Customer Information
              </CardTitle>
              <CardDescription>Enter customer details for the sale</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="customerName">Customer Name *</Label>
                <Input
                  id="customerName"
                  placeholder="Enter customer name"
                  value={customer.name}
                  onChange={(e) => setCustomer({ ...customer, name: e.target.value })}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="customerPhone">Telephone *</Label>
                <Input
                  id="customerPhone"
                  placeholder="077XXXXXXX"
                  value={customer.telephone}
                  onChange={(e) => setCustomer({ ...customer, telephone: e.target.value })}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="customerAddress">Address *</Label>
                <Input
                  id="customerAddress"
                  placeholder="Enter full address"
                  value={customer.address}
                  onChange={(e) => setCustomer({ ...customer, address: e.target.value })}
                />
              </div>
            </CardContent>
          </Card>

          {/* Add Items */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <ShoppingCart className="h-5 w-5" />
                Add Items
              </CardTitle>
              <CardDescription>Select items to add to the sale</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="flex gap-2">
                <Select value={selectedItemId} onValueChange={setSelectedItemId}>
                  <SelectTrigger className="flex-1">
                    <SelectValue placeholder="Select an item..." />
                  </SelectTrigger>
                  <SelectContent>
                    {items.map((item) => (
                      <SelectItem key={item.itemId} value={item.itemId.toString()}>
                        <div className="flex items-center justify-between w-full">
                          <span>{item.name}</span>
                          <span className="text-success font-medium ml-2">LKR {item.unitPrice}</span>
                        </div>
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                <Button onClick={addItemToSale} disabled={!selectedItemId}>
                  <Plus className="h-4 w-4" />
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Right Column - Sale Summary */}
        <Card>
          <CardHeader>
            <CardTitle>Sale Summary</CardTitle>
            <CardDescription>Review items and total before completing sale</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            {/* Sale Items */}
            <div className="space-y-3 max-h-64 overflow-y-auto">
              {saleItems.length === 0 ? (
                <div className="text-center py-8 text-muted-foreground">
                  <ShoppingCart className="h-8 w-8 mx-auto mb-2 opacity-50" />
                  <p>No items added yet</p>
                </div>
              ) : (
                saleItems.map((saleItem) => (
                  <div key={saleItem.item.itemId} className="flex items-center justify-between p-3 bg-accent/20 rounded-lg">
                    <div className="flex-1">
                      <p className="font-medium text-sm">{saleItem.item.name}</p>
                      <p className="text-xs text-muted-foreground">{saleItem.item.categoryName}</p>
                      <p className="text-xs text-success">LKR {saleItem.item.unitPrice} each</p>
                      {saleItem.discountAmount > 0 && (
                        <Badge variant="secondary" className="text-xs mt-1">
                          {saleItem.item.discount}% discount applied
                        </Badge>
                      )}
                    </div>
                    <div className="flex items-center gap-2">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => updateItemQuantity(saleItem.item.itemId, saleItem.qty - 1)}
                      >
                        <Minus className="h-3 w-3" />
                      </Button>
                      <span className="w-8 text-center font-medium">{saleItem.qty}</span>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => updateItemQuantity(saleItem.item.itemId, saleItem.qty + 1)}
                      >
                        <Plus className="h-3 w-3" />
                      </Button>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => removeItemFromSale(saleItem.item.itemId)}
                        className="text-destructive hover:text-destructive"
                      >
                        <Trash2 className="h-3 w-3" />
                      </Button>
                    </div>
                  </div>
                ))
              )}
            </div>

            {saleItems.length > 0 && (
              <>
                <Separator />
                {/* Totals */}
                <div className="space-y-2">
                  <div className="flex justify-between text-sm">
                    <span>Subtotal:</span>
                    <span>LKR {subTotal.toFixed(2)}</span>
                  </div>
                  {totalDiscount > 0 && (
                    <div className="flex justify-between text-sm text-success">
                      <span>Total Discount:</span>
                      <span>-LKR {totalDiscount.toFixed(2)}</span>
                    </div>
                  )}
                  <Separator />
                  <div className="flex justify-between text-lg font-bold">
                    <span>Total Amount:</span>
                    <span className="text-success">LKR {totalAmount.toFixed(2)}</span>
                  </div>
                </div>

                <Button 
                  onClick={handleCreateSale}
                  className="w-full bg-gradient-to-r from-primary to-success hover:from-primary/90 hover:to-success/90"
                  size="lg"
                >
                  <Receipt className="h-4 w-4 mr-2" />
                  Complete Sale
                </Button>
              </>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
};