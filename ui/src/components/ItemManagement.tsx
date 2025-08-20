import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Search, Plus, Package, Edit2, Eye, Tag, DollarSign, Hash } from 'lucide-react';
import { mockApi,  formatCurrency } from '@/services/mockApi';
import { useToast } from '@/hooks/use-toast';
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from '@/components/ui/pagination';
import { createCategory, getCategories } from '@/services/categoryService'; 
import { Category } from '@/types/Category';
import { PaginatedResponse } from '@/types/PaginatedResponse';
import { createItem, getItems, getItemsByCategory, searchItems } from '@/services/itemService';
import { Item } from '@/types/Item';

const ItemManagement = () => {
  const [items, setItems] = useState<Item[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string>('all');
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
  const [isViewDialogOpen, setIsViewDialogOpen] = useState(false);
  const [isCategoryDialogOpen, setIsCategoryDialogOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState<Item | null>(null);
  const [newItem, setNewItem] = useState({
    name: '',
    unitPrice: '',
    stockAvailable: '',
    discount: '',
    qtyToAllowDiscount: '',
    categoryId: ''
  });
  const [newCategoryName, setNewCategoryName] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const { toast } = useToast();

  const itemsPerPage = 6;

  // Load items with pagination
  const loadItems = async (page: number = 1, search?: string, categoryId?: number) => {
    setLoading(true);
    try {
        let response: PaginatedResponse<Item>;
      if (search) {
        response = await searchItems(search.toLowerCase(),page); // Replace with backend search API if exists
        response.data = response.data.filter(i => i.name.toLowerCase().includes(search.toLowerCase()));
      } else if (categoryId) {
        response = await getItemsByCategory(page); // Replace with category API if needed
        response.data = response.data.filter(i => i.categoryId === categoryId);
      } else {
        response = await getItems(page);
      }
      setItems(response.data);
      setTotalPages(response.totalPages);
      setCurrentPage(page);
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to load items",
        variant: "destructive"
      });
    } finally {
      setLoading(false);
    }
  };

  // Load categories
  const loadCategories = async () => {
    try {
      const response = await getCategories();
      setCategories(response);
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to load categories",
        variant: "destructive"
      });
    }
  };

  useEffect(() => {
    loadCategories();
  }, []);

  useEffect(() => {
    const categoryId = selectedCategory === 'all' ? undefined : parseInt(selectedCategory);
    loadItems(1, searchTerm, categoryId);
  }, [searchTerm, selectedCategory]);

  const handleSearch = (value: string) => {
    setSearchTerm(value);
    setCurrentPage(1);
  };

  const handleCategoryChange = (value: string) => {
    setSelectedCategory(value);
    setCurrentPage(1);
  };

  const handlePageChange = (page: number) => {
    const categoryId = selectedCategory === 'all' ? undefined : parseInt(selectedCategory);
    loadItems(page, searchTerm, categoryId);
  };

  const handleAddItem = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!newItem.name || !newItem.unitPrice || !newItem.stockAvailable || !newItem.categoryId) {
      toast({
        title: "Error",
        description: "Please fill in all required fields",
        variant: "destructive"
      });
      return;
    }

    try {
      await createItem({
        name: newItem.name,
        unitPrice: parseFloat(newItem.unitPrice),
        stockAvailable: parseInt(newItem.stockAvailable),
        discount: parseFloat(newItem.discount) || 0,
        qtyToAllowDiscount: parseInt(newItem.qtyToAllowDiscount) || 1,
        categoryId: parseInt(newItem.categoryId)
      });
      
      toast({
        title: "Success",
        description: "Item added successfully"
      });
      
      setNewItem({
        name: '',
        unitPrice: '',
        stockAvailable: '',
        discount: '',
        qtyToAllowDiscount: '',
        categoryId: ''
      });
      setIsDialogOpen(false);
      loadItems(currentPage, searchTerm, selectedCategory === 'all' ? undefined : parseInt(selectedCategory));
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to add item",
        variant: "destructive"
      });
    }
  };

  const handleEditItem = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!selectedItem || !newItem.name || !newItem.unitPrice || !newItem.stockAvailable || !newItem.categoryId) {
      toast({
        title: "Error",
        description: "Please fill in all required fields",
        variant: "destructive"
      });
      return;
    }

    try {
      await mockApi.updateItem(selectedItem.itemId, {
        name: newItem.name,
        unitPrice: parseFloat(newItem.unitPrice),
        stockAvailable: parseInt(newItem.stockAvailable),
        discount: parseFloat(newItem.discount) || 0,
        qtyToAllowDiscount: parseInt(newItem.qtyToAllowDiscount) || 1,
        categoryId: parseInt(newItem.categoryId)
      });
      
      toast({
        title: "Success",
        description: "Item updated successfully"
      });
      
      setIsEditDialogOpen(false);
      setSelectedItem(null);
      loadItems(currentPage, searchTerm, selectedCategory === 'all' ? undefined : parseInt(selectedCategory));
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to update item",
        variant: "destructive"
      });
    }
  };

  const handleAddCategory = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!newCategoryName.trim()) {
      toast({
        title: "Error",
        description: "Please enter a category name",
        variant: "destructive"
      });
      return;
    }

    try {
      await createCategory({name:newCategoryName});
      
      toast({
        title: "Success",
        description: "Category added successfully"
      });
      
      setNewCategoryName('');
      setIsCategoryDialogOpen(false);
      loadCategories();
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to add category",
        variant: "destructive"
      });
    }
  };

  const openEditDialog = (item: Item) => {
    setSelectedItem(item);
    setNewItem({
      name: item.name,
      unitPrice: item.unitPrice.toString(),
      stockAvailable: item.stockAvailable.toString(),
      discount: item.discount.toString(),
      qtyToAllowDiscount: item.qtyToAllowDiscount.toString(),
      categoryId: item.categoryId.toString()
    });
    setIsEditDialogOpen(true);
  };

  const openViewDialog = (item: Item) => {
    setSelectedItem(item);
    setIsViewDialogOpen(true);
  };

  const getStockStatus = (stock: number) => {
    if (stock === 0) return { status: "Out of Stock", variant: "destructive" as const };
    if (stock < 20) return { status: "Low Stock", variant: "secondary" as const };
    return { status: "In Stock", variant: "default" as const };
  };

  const getCategoryName = (categoryId: number) => {
    const category = categories.find(c => c.categoryId === categoryId);
    return category ? category.name : 'Unknown';
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-foreground">Item Management</h1>
          <p className="text-muted-foreground mt-1">Manage inventory and item information</p>
        </div>
        <div className="flex gap-2">
          <Dialog open={isCategoryDialogOpen} onOpenChange={setIsCategoryDialogOpen}>
            <DialogTrigger asChild>
              <Button variant="outline">
                <Tag className="h-4 w-4 mr-2" />
                Add Category
              </Button>
            </DialogTrigger>
          </Dialog>
          <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
            <DialogTrigger asChild>
              <Button className="bg-gradient-to-r from-primary to-success hover:from-primary/90 hover:to-success/90">
                <Plus className="h-4 w-4 mr-2" />
                Add Item
              </Button>
            </DialogTrigger>
          </Dialog>
        </div>
      </div>

      {/* Filters */}
      <div className="flex flex-col sm:flex-row gap-4">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground h-4 w-4" />
          <Input
            placeholder="Search items..."
            value={searchTerm}
            onChange={(e) => handleSearch(e.target.value)}
            className="pl-10"
          />
        </div>
        <Select value={selectedCategory} onValueChange={handleCategoryChange}>
          <SelectTrigger className="w-full sm:w-[200px]">
            <SelectValue placeholder="Filter by category" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">All Categories</SelectItem>
            {categories.map((category) => (
              <SelectItem key={category.categoryId} value={category.categoryId.toString()}>
                {category.name}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      {/* Summary Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <CardContent className="p-4">
            <div className="flex items-center gap-3">
              <Package className="h-8 w-8 text-primary" />
              <div>
                <p className="text-sm text-muted-foreground">Total Items</p>
                <p className="text-2xl font-bold">{items.length}</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <div className="flex items-center gap-3">
              <Hash className="h-8 w-8 text-success" />
              <div>
                <p className="text-sm text-muted-foreground">In Stock</p>
                <p className="text-2xl font-bold">{items.reduce((sum, item) => sum + item.stockAvailable, 0)}</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <div className="flex items-center gap-3">
              <Tag className="h-8 w-8 text-warning" />
              <div>
                <p className="text-sm text-muted-foreground">Categories</p>
                <p className="text-2xl font-bold">{categories.length}</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <div className="flex items-center gap-3">
              <DollarSign className="h-8 w-8 text-info" />
              <div>
                <p className="text-sm text-muted-foreground">Avg. Price</p>
                <p className="text-2xl font-bold">
                  {items.length > 0 ? formatCurrency(items.reduce((sum, item) => sum + item.unitPrice, 0) / items.length) : 'LKR 0.00'}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {loading ? (
        <div className="text-center py-12">
          <p className="text-muted-foreground">Loading items...</p>
        </div>
      ) : (
        <>
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
            {items.length > 0 ? (
              items.map((item) => {
                const stockInfo = getStockStatus(item.stockAvailable);
                return (
                  <Card key={item.itemId} className="hover:shadow-md transition-shadow">
                    <CardHeader className="pb-3">
                      <div className="flex items-start justify-between">
                        <div className="flex-1">
                          <CardTitle className="text-lg leading-tight">{item.name}</CardTitle>
                          <Badge variant="outline" className="mt-1">
                            {getCategoryName(item.categoryId)}
                          </Badge>
                        </div>
                      </div>
                    </CardHeader>
                    <CardContent className="space-y-4">
                      <div className="grid grid-cols-2 gap-4 text-sm">
                        <div>
                          <p className="text-muted-foreground">Unit Price</p>
                          <p className="font-bold text-lg text-success">{formatCurrency(item.unitPrice)}</p>
                        </div>
                        <div>
                          <p className="text-muted-foreground">Stock</p>
                          <div className="flex items-center gap-2">
                            <p className="font-bold">{item.stockAvailable}</p>
                            <Badge variant={stockInfo.variant} className="text-xs">
                              {stockInfo.status}
                            </Badge>
                          </div>
                        </div>
                      </div>
                      
                      {item.discount > 0 && (
                        <div className="p-3 bg-gradient-to-r from-accent/20 to-secondary/20 rounded-lg">
                          <p className="text-sm font-medium text-foreground">
                            {item.discount}% discount on {item.qtyToAllowDiscount}+ items
                          </p>
                        </div>
                      )}

                      <div className="flex gap-2 pt-2">
                        <Button
                          size="sm"
                          variant="outline"
                          onClick={() => openViewDialog(item)}
                          className="flex-1"
                        >
                          <Eye className="h-4 w-4 mr-1" />
                          View
                        </Button>
                        <Button
                          size="sm"
                          variant="outline"
                          onClick={() => openEditDialog(item)}
                          className="flex-1"
                        >
                          <Edit2 className="h-4 w-4 mr-1" />
                          Edit
                        </Button>
                      </div>
                    </CardContent>
                  </Card>
                );
              })
            ) : (
              <div className="col-span-full text-center py-12">
                <p className="text-muted-foreground">
                  {searchTerm || selectedCategory !== 'all' ? 'No items found matching your criteria.' : 'No items found.'}
                </p>
              </div>
            )}
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="flex justify-center mt-8">
              <Pagination>
                <PaginationContent>
                  <PaginationItem>
                    <PaginationPrevious 
                      onClick={() => currentPage > 1 && handlePageChange(currentPage - 1)}
                      className={currentPage === 1 ? "pointer-events-none opacity-50" : "cursor-pointer"}
                    />
                  </PaginationItem>
                  {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                    <PaginationItem key={page}>
                      <PaginationLink
                        onClick={() => handlePageChange(page)}
                        isActive={currentPage === page}
                        className="cursor-pointer"
                      >
                        {page}
                      </PaginationLink>
                    </PaginationItem>
                  ))}
                  <PaginationItem>
                    <PaginationNext 
                      onClick={() => currentPage < totalPages && handlePageChange(currentPage + 1)}
                      className={currentPage === totalPages ? "pointer-events-none opacity-50" : "cursor-pointer"}
                    />
                  </PaginationItem>
                </PaginationContent>
              </Pagination>
            </div>
          )}
        </>
      )}

      {/* Add Category Dialog */}
      <Dialog open={isCategoryDialogOpen} onOpenChange={setIsCategoryDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Add New Category</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleAddCategory} className="space-y-4">
            <div>
              <Label htmlFor="categoryName">Category Name</Label>
              <Input
                id="categoryName"
                value={newCategoryName}
                onChange={(e) => setNewCategoryName(e.target.value)}
                placeholder="Enter category name"
                required
              />
            </div>
            <div className="flex gap-2 pt-4">
              <Button type="button" variant="outline" onClick={() => setIsCategoryDialogOpen(false)} className="flex-1">
                Cancel
              </Button>
              <Button type="submit" className="flex-1">
                Add Category
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>

      {/* Add Item Dialog */}
      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Add New Item</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleAddItem} className="space-y-4">
            <div>
              <Label htmlFor="itemName">Item Name</Label>
              <Input
                id="itemName"
                value={newItem.name}
                onChange={(e) => setNewItem({ ...newItem, name: e.target.value })}
                placeholder="Enter item name"
                required
              />
            </div>
            <div>
              <Label htmlFor="unitPrice">Unit Price (LKR )</Label>
              <Input
                id="unitPrice"
                type="number"
                step="0.01"
                value={newItem.unitPrice}
                onChange={(e) => setNewItem({ ...newItem, unitPrice: e.target.value })}
                placeholder="0.00"
                required
              />
            </div>
            <div>
              <Label htmlFor="stock">Stock Available</Label>
              <Input
                id="stock"
                type="number"
                value={newItem.stockAvailable}
                onChange={(e) => setNewItem({ ...newItem, stockAvailable: e.target.value })}
                placeholder="0"
                required
              />
            </div>
            <div>
              <Label htmlFor="category">Category</Label>
              <Select value={newItem.categoryId} onValueChange={(value) => setNewItem({ ...newItem, categoryId: value })}>
                <SelectTrigger>
                  <SelectValue placeholder="Select category" />
                </SelectTrigger>
                <SelectContent>
                  {categories.map((category) => (
                    <SelectItem key={category.categoryId} value={category.categoryId.toString()}>
                      {category.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div>
              <Label htmlFor="discount">Discount (%)</Label>
              <Input
                id="discount"
                type="number"
                step="0.01"
                value={newItem.discount}
                onChange={(e) => setNewItem({ ...newItem, discount: e.target.value })}
                placeholder="0.00"
              />
            </div>
            <div>
              <Label htmlFor="qtyDiscount">Quantity for Discount</Label>
              <Input
                id="qtyDiscount"
                type="number"
                value={newItem.qtyToAllowDiscount}
                onChange={(e) => setNewItem({ ...newItem, qtyToAllowDiscount: e.target.value })}
                placeholder="1"
              />
            </div>
            <div className="flex gap-2 pt-4">
              <Button type="button" variant="outline" onClick={() => setIsDialogOpen(false)} className="flex-1">
                Cancel
              </Button>
              <Button type="submit" className="flex-1">
                Add Item
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>

      {/* Edit Item Dialog */}
      <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Edit Item</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleEditItem} className="space-y-4">
            <div>
              <Label htmlFor="editItemName">Item Name</Label>
              <Input
                id="editItemName"
                value={newItem.name}
                onChange={(e) => setNewItem({ ...newItem, name: e.target.value })}
                placeholder="Enter item name"
                required
              />
            </div>
            <div>
              <Label htmlFor="editUnitPrice">Unit Price (LKR )</Label>
              <Input
                id="editUnitPrice"
                type="number"
                step="0.01"
                value={newItem.unitPrice}
                onChange={(e) => setNewItem({ ...newItem, unitPrice: e.target.value })}
                placeholder="0.00"
                required
              />
            </div>
            <div>
              <Label htmlFor="editStock">Stock Available</Label>
              <Input
                id="editStock"
                type="number"
                value={newItem.stockAvailable}
                onChange={(e) => setNewItem({ ...newItem, stockAvailable: e.target.value })}
                placeholder="0"
                required
              />
            </div>
            <div>
              <Label htmlFor="editCategory">Category</Label>
              <Select value={newItem.categoryId} onValueChange={(value) => setNewItem({ ...newItem, categoryId: value })}>
                <SelectTrigger>
                  <SelectValue placeholder="Select category" />
                </SelectTrigger>
                <SelectContent>
                  {categories.map((category) => (
                    <SelectItem key={category.categoryId} value={category.categoryId.toString()}>
                      {category.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div>
              <Label htmlFor="editDiscount">Discount (%)</Label>
              <Input
                id="editDiscount"
                type="number"
                step="0.01"
                value={newItem.discount}
                onChange={(e) => setNewItem({ ...newItem, discount: e.target.value })}
                placeholder="0.00"
              />
            </div>
            <div>
              <Label htmlFor="editQtyDiscount">Quantity for Discount</Label>
              <Input
                id="editQtyDiscount"
                type="number"
                value={newItem.qtyToAllowDiscount}
                onChange={(e) => setNewItem({ ...newItem, qtyToAllowDiscount: e.target.value })}
                placeholder="1"
              />
            </div>
            <div className="flex gap-2 pt-4">
              <Button type="button" variant="outline" onClick={() => setIsEditDialogOpen(false)} className="flex-1">
                Cancel
              </Button>
              <Button type="submit" className="flex-1">
                Update Item
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>

      {/* View Item Dialog */}
      <Dialog open={isViewDialogOpen} onOpenChange={setIsViewDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Item Details</DialogTitle>
          </DialogHeader>
          {selectedItem && (
            <div className="space-y-4">
              <div>
                <Label className="text-sm font-medium text-muted-foreground">Item Name</Label>
                <p className="text-base">{selectedItem.name}</p>
              </div>
              <div>
                <Label className="text-sm font-medium text-muted-foreground">Category</Label>
                <p className="text-base">{getCategoryName(selectedItem.categoryId)}</p>
              </div>
              <div>
                <Label className="text-sm font-medium text-muted-foreground">Unit Price</Label>
                <p className="text-base font-semibold text-success">{formatCurrency(selectedItem.unitPrice)}</p>
              </div>
              <div>
                <Label className="text-sm font-medium text-muted-foreground">Stock Available</Label>
                <p className="text-base">{selectedItem.stockAvailable} units</p>
              </div>
              {selectedItem.discount > 0 && (
                <>
                  <div>
                    <Label className="text-sm font-medium text-muted-foreground">Discount</Label>
                    <p className="text-base">{selectedItem.discount}%</p>
                  </div>
                  <div>
                    <Label className="text-sm font-medium text-muted-foreground">Minimum Quantity for Discount</Label>
                    <p className="text-base">{selectedItem.qtyToAllowDiscount} units</p>
                  </div>
                </>
              )}
              <div>
                <Label className="text-sm font-medium text-muted-foreground">Last Updated</Label>
                <p className="text-base">{new Date(selectedItem.lastUpdatedAt).toLocaleString()}</p>
              </div>
              <div className="flex gap-2 pt-4">
                <Button type="button" variant="outline" onClick={() => setIsViewDialogOpen(false)} className="flex-1">
                  Close
                </Button>
                <Button 
                  onClick={() => {
                    setIsViewDialogOpen(false);
                    openEditDialog(selectedItem);
                  }}
                  className="flex-1"
                >
                  Edit Item
                </Button>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
};

export { ItemManagement };