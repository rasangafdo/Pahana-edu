import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import {
  Plus,
  Minus,
  ShoppingCart,
  Receipt,
  Trash2,
  User,
  Phone,
  DollarSign,
} from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import { searchItems } from "@/services/itemService";
import { Item } from "@/types/Item";
import { Customer } from "@/types/Customer";
import { SaleItem } from "@/types/SaleItem";
import { getCategoryById } from "@/services/categoryService";
import { getCustomerByTelephone } from "@/services/customerService";
import { createSale } from "@/services/saleService";
import { handlePrint } from "@/lib/utils";


export const SalesBilling = () => {
  const [items,setItems] = useState<Item[]>([]);

  const [customer, setCustomer] = useState<Customer>({
    name: "",
    telephone: "",
    address: "",
  });

  const [saleItems, setSaleItems] = useState<SaleItem[]>([]);
  const [selectedItemId, setSelectedItemId] = useState<string>("");
  const [paidAmount, setPaidAmount] = useState<string>("");
  const [search, setSearch] = useState<string>("");
  const [isLockCustomer, setIsLockCustomer] = useState<boolean>(false);
  const { toast } = useToast();

  const addItemToSale = () => {
    if (!selectedItemId) return;

    const item = items.find((i) => i.itemId.toString() === selectedItemId);
    if (!item) return;

    const existingItem = saleItems.find((si) => si.item.itemId === item.itemId);
    if (existingItem) {
      updateItemQuantity(item.itemId, existingItem.qty + 1);
    } else {
      const newSaleItem: SaleItem = {
        item,
        qty: 1,
        discountAmount: 0,
        itemTotal: item.unitPrice,
      };
      setSaleItems([...saleItems, newSaleItem]);
    }
    setSelectedItemId("");
  setItems([])
    setSearch("")
  };

  const updateItemQuantity = (itemId: number, newQty: number) => {
    if (newQty <= 0) { 
      newQty = 1
    }
    const item = saleItems.find((i) => i.item.itemId === itemId).item;
    if(!item) return;
    if(newQty>item.stockAvailable){
toast({
        title: "Error",
        description: `Only ${item.stockAvailable} units available in stock.`,
        variant: "destructive",
      })
      return;
    }else{
      toast({}).dismiss()
    }
 
    setSaleItems(
      saleItems.map((saleItem) => {
        if (saleItem.item.itemId === itemId) {
          const hasDiscount = newQty >= saleItem.item.qtyToAllowDiscount;
          const discountAmount = hasDiscount
            ? (saleItem.item.unitPrice * newQty * saleItem.item.discount) / 100
            : 0;
          const itemTotal = saleItem.item.unitPrice * newQty - discountAmount;

          return {
            ...saleItem,
            qty: newQty,
            discountAmount,
            itemTotal,
          };
        }
        return saleItem;
      })
    );
  };

  const removeItemFromSale = (itemId: number) => {
    setSaleItems(
      saleItems.filter((saleItem) => saleItem.item.itemId !== itemId)
    );
  };

  const handleItemSearch = async(searchTerm: string) => {
   try{
     if (!searchTerm) {
      setItems([]); // Reset items if search is empty
      return;
    }
    // Simulate API call to search items
    const filteredItems = await searchItems(searchTerm);
    setItems(filteredItems.data);
    } catch (error) {
      console.error("Error searching items:", error);
      toast({
        title: "Error",
        description: "Failed to search items",
        variant: "destructive",
      });
    }
  };


  const calculateTotals = () => {
    const subTotal = saleItems.reduce(
      (sum, item) => sum + item.item.unitPrice * item.qty,
      0
    );
    const totalDiscount = saleItems.reduce(
      (sum, item) => sum + item.discountAmount,
      0
    );
    const totalAmount = subTotal - totalDiscount;
    const paid = parseFloat(paidAmount) || 0;
    const balance = totalAmount - paid;

    return { subTotal, totalDiscount, totalAmount, paid, balance };
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

    try{
       const sale = await createSale({customer, saleItems,paid, balance});
      if (!sale) {
        toast({
          title: "Error",
          description: "Failed to create sale",
          variant: "destructive",
        });
        return;
      }
  handlePrint(
        sale.customerId,
        saleItems,
        subTotal,
        totalDiscount,
        totalAmount,
        paid,
        balance,
        toast
      );

        toast({
      title: "Success",
      description: "Sale created successfully",
      variant: "default",
    });
      setCustomer({ name: "", telephone: "", address: "" });
      setSaleItems([]);
      setPaidAmount("");
      setSearch("")
    }catch (error) {
      console.error("Error creating sale:", error);
      toast({
        title: "Error",
        description: "Failed to create sale",
                variant: "destructive",
      });
      return;
    }
  
  };

  const handleCustomerFetch = async (phone: string) => {

    if(!phone) {
      setCustomer({ name: "", telephone: "", address: "" });
      return;
    }
    if (phone.trim().length < 9) {
      setCustomer({ name: "", telephone: phone, address: "" });
      return;
    } 
    try {
      const fetchedCustomer = await getCustomerByTelephone(phone);
      if (fetchedCustomer) {
        setCustomer(fetchedCustomer);
        setIsLockCustomer(true);
        toast({}).dismiss()
      } else {
        setIsLockCustomer(false);

      setCustomer({ name: "", telephone: phone, address: "" });
        toast({
          title: "Customer Not Found",
          description: "This will create a new customer",
          variant: "default",
        });
      }
    } catch (error) {
      console.error("Error fetching customer:", error);
        setIsLockCustomer(false);

      toast({
        title: "Error",
        description: "Failed to fetch customer details",
        variant: "destructive",
      });
      setCustomer({ name: "", telephone: phone, address: "" });
    }
  };

  const { subTotal, totalDiscount, totalAmount, paid, balance } = calculateTotals();




  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-foreground">
            Sales & Billing
          </h1>
          <p className="text-muted-foreground mt-1">
            Create new sales and generate bills
          </p>
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
              <CardDescription>
                Enter customer details for the sale
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              
              <div className="space-y-2">
                <Label htmlFor="customerPhone">Telephone *</Label>
                <Input
                  id="customerPhone"
                  placeholder="077XXXXXXX"
                  value={customer.telephone}
                  onChange={(e) =>
                    handleCustomerFetch(e.target.value)
                  }
                />
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="customerName">Customer Name *</Label>
                <Input
                  id="customerName"
                  placeholder="Enter customer name"
                  value={customer.name}
                  onChange={(e) =>{ if(!isLockCustomer) {
                    setCustomer({ ...customer, name: e.target.value })
                  }
                  }
                }
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="customerAddress">Address *</Label>
                <Input
                  id="customerAddress"
                  placeholder="Enter full address"
                  value={customer.address}
                  onChange={(e) =>{ if(!isLockCustomer) {
                    setCustomer({ ...customer, address: e.target.value })
                  }}}
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
                <Select
                  value={selectedItemId}
                  onValueChange={setSelectedItemId}
                >
                  <SelectTrigger className="flex-1">
                    <SelectValue placeholder="Select an item..." />
                  </SelectTrigger>
                  <SelectContent>
                    {/* 🔎 Search Input inside dropdown */}
                    <div className="p-2">
                      <Input id="search"
                        type="text"
                        value={search}
                        onChange={(e) => {
                          setSearch(e.target.value)
                          handleItemSearch(e.target.value)
                        }}
                        placeholder="Search..."
                        className="w-full rounded-md border px-2 py-1 text-sm focus:outline-none focus:ring-2 focus:ring-ring"
                      />
                    </div>
                    {items.map((item) => (
                      <SelectItem
                        key={item.itemId}
                        value={item.itemId.toString()}
                      >
                        <div className="flex items-center justify-between w-full">
                          <span>{item.name}</span>
                          <span className="text-success font-medium ml-2">
                            LKR {item.unitPrice}
                          </span> 
                          <span className="text-xs ml-6">stock : {item.stockAvailable}</span>
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
            <CardDescription>
              Review items and total before completing sale
            </CardDescription>
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
                  <div
                    key={saleItem.item.itemId}
                    className="flex items-center justify-between p-3 bg-accent/20 rounded-lg"
                  >
                    <div className="flex-1">
                      <p className="font-medium text-sm">
                        {saleItem.item.name}
                      </p> 
                      <p className="text-xs text-success">
                        LKR {saleItem.item.unitPrice.toFixed(2)} each
                      </p>
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
                        onClick={() =>
                          updateItemQuantity(
                            saleItem.item.itemId,
                            saleItem.qty - 1
                          )
                        }
                      >
                        <Minus className="h-3 w-3" />
                      </Button> 
                      <span className="w-8 text-center font-medium">
                        {saleItem.qty}
                      </span>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() =>
                          updateItemQuantity(
                            saleItem.item.itemId,
                            saleItem.qty + 1
                          )
                        }
                      >
                        <Plus className="h-3 w-3" />
                      </Button>
                        <Input
                        id="qty"
                        type="number"
                        placeholder="1"
                        min={1} 
                        onChange={(e) =>
                          updateItemQuantity(saleItem.item.itemId, Number(e.target.value)) 
                        } 
                        className="max-w-12 text-center font-medium [appearance:textfield] [&::-webkit-outer-spin-button]:appearance-none [&::-webkit-inner-spin-button]:appearance-none"
                      />
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
                <div className="space-y-4 p-4 bg-accent/20 rounded-lg">
                  <div className="flex items-center gap-2">
                    <DollarSign className="h-4 w-4 text-success" />
                    <Label htmlFor="paidAmount" className="font-medium">
                      Payment Details
                    </Label>
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="paidAmount">Paid Amount (LKR)</Label>
                    <Input
                      id="paidAmount"
                      type="number"
                      step="0.01"
                      placeholder="0.00"
                      value={paidAmount}
                      onChange={(e) => setPaidAmount(e.target.value)}
                      className="text-right"
                    />
                  </div>
                </div>

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
                    <span className="text-success">
                      LKR {totalAmount.toFixed(2)}
                    </span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span>Paid:</span>
                    <span className="text-info">LKR {paid.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between text-sm font-medium">
                    <span>Balance:</span>
                    <span
                      className={
                        balance > 0
                          ? "text-warning"
                          : balance < 0
                          ? "text-success"
                          : "text-muted-foreground"
                      }
                    >
                      LKR {balance.toFixed(2)}
                    </span>
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
