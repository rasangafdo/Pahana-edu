import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Search, Plus, Phone, MapPin, Calendar, Edit2, Eye, Trash2, User, Mail } from 'lucide-react';
 
import { useToast } from '@/hooks/use-toast';
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from '@/components/ui/pagination';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';
import { createStaff, deleteStaff, getAllStaff, getUserByUsername, updateStaff } from '@/services/authService';
import { Staff } from '@/types/Staff'; 

const StaffManagement = () => {
  const [staff, setStaff] = useState<Staff[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
  const [isViewDialogOpen, setIsViewDialogOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const [selectedStaff, setSelectedStaff] = useState<Staff | null>(null);
  const [newStaff, setNewStaff] = useState<Staff>({
    name: '',
    telephone: '',
    address: '',
    username: '',
    email: '',
    password: '',
    role: 'CASHIER', // Default role
  });
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const { toast } = useToast(); 

  const itemsPerPage = 6;

  // Load staff with pagination
  const loadStaff = async (  search?: string) => {
    setLoading(true);
    try { 
      if(search){
        const response  = await getUserByUsername(search );
        setStaff(response ? [response] : []);
      }else{
       const response = await getAllStaff();
      setStaff(response);
      }
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to load staff",
        variant: "destructive"
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadStaff(searchTerm);
  }, [searchTerm]);

  const handleSearch = (value: string) => {
    setSearchTerm(value);
    setCurrentPage(1);
  };
 
  const handleAddStaff = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!newStaff.name || !newStaff.telephone || !newStaff.address || !newStaff.username || !newStaff.email || !newStaff.password || !newStaff.role) {
      toast({
        title: "Error",
        description: "Please fill in all fields",
        variant: "destructive"
      });
      return;
    }

    try {
      
      await createStaff(newStaff );
      
      toast({
        title: "Success",
        description: "Staff member added successfully",
        variant: "default"
      });
      
      setNewStaff({
        name: '',
        telephone: '',
        address: '',
        username: '',
        email: '',
        password: '',
        role: 'CASHIER', // Reset to default role
      });
      setIsDialogOpen(false);
      loadStaff(searchTerm);
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to add staff member",
        variant: "destructive"
      });
    }
  };
 

  const openEditDialog = (staffMember: Staff) => {
    setSelectedStaff(staffMember);
    setNewStaff({
      name: staffMember.name,
      telephone: staffMember.telephone,
      address: staffMember.address,
      username: staffMember.username,
      email: staffMember.email,
      password: '', // Don't populate password
      role: staffMember.role
    });
    setIsEditDialogOpen(true);
  };

  const openViewDialog = (staffMember: Staff) => {
    setSelectedStaff(staffMember);
    setIsViewDialogOpen(true);
  };

  const openDeleteDialog = (staffMember: Staff) => {
    setSelectedStaff(staffMember);
    setIsDeleteDialogOpen(true);
  };

  const handleDeleteStaff = async () => {
    if (!selectedStaff) return;

    try {
      await deleteStaff(selectedStaff.id );
      
      toast({
        title: "Success",
        description: "Staff member deleted successfully",
        variant: "default"
      });
      
      setIsDeleteDialogOpen(false);
      setSelectedStaff(null);
      loadStaff(searchTerm);
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to delete staff member",
        variant: "destructive"
      });
    }
  };

    const handleEditStaff = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!selectedStaff || !newStaff.name || !newStaff.telephone || !newStaff.address || !newStaff.username || !newStaff.email || !newStaff.role) {
      toast({
        title: "Error",
        description: "Please fill in all required fields",
        variant: "destructive"
      });
      return;
    }

    try {
      const updateData = { ...newStaff };
      if (!updateData.password) {
        delete updateData.password; // Don't update password if empty
      }
      
      await  updateStaff(selectedStaff.id, updateData);
      
      toast({
        title: "Success",
        description: "Staff member updated successfully"
      });
      
      setIsEditDialogOpen(false);
      setSelectedStaff(null);
      loadStaff(searchTerm);
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to update staff member",
        variant: "destructive"
      });
    }
  };


  const getRoleBadgeVariant = (role: string) => {
    switch (role.toUpperCase()) {
      case 'MANAGER':
        return 'default';
      case 'CASHIER':
        return 'secondary';
      case 'ASSISTANT':
        return 'outline';
      default:
        return 'outline';
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-foreground">Staff Management</h1>
          <p className="text-muted-foreground mt-1">Manage staff members and their access</p>
        </div>
        <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
          <DialogTrigger asChild>
            <Button className="bg-gradient-to-r from-primary to-success hover:from-primary/90 hover:to-success/90">
              <Plus className="h-4 w-4 mr-2" />
              Add Staff
            </Button>
          </DialogTrigger>
        </Dialog>
      </div>

      {/* Search */}
      <div className="flex items-center gap-4">
        <div className="relative w-full max-w-sm">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground h-4 w-4" />
          <Input
            placeholder="Search staff..."
            value={searchTerm}
            onChange={(e) => handleSearch(e.target.value)}
            className="pl-10"
          />
        </div>
      </div>

      {loading ? (
        <div className="text-center py-12">
          <p className="text-muted-foreground">Loading staff...</p>
        </div>
      ) : (
        <>
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
            {staff.length > 0 ? (
              staff.map((staffMember) => (
                <Card key={staffMember.id} className="hover:shadow-md transition-shadow">
                  <CardHeader className="pb-3">
                    <div className="flex items-start justify-between">
                      <CardTitle className="text-lg">{staffMember.name}</CardTitle>
                      <div className="flex flex-col gap-1">
                        <Badge variant={getRoleBadgeVariant(staffMember.role)}>
                          {staffMember.role}
                        </Badge>
                        <Badge variant={staffMember.isActive ? "default" : "secondary"} className="text-xs">
                          {staffMember.isActive ? "Active" : "Inactive"}
                        </Badge>
                      </div>
                    </div>
                  </CardHeader>
                  <CardContent className="space-y-3">
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                      <User className="h-4 w-4" />
                      <span>{staffMember.username}</span>
                    </div>
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                      <Mail className="h-4 w-4" />
                      <span>{staffMember.email}</span>
                    </div>
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                      <Phone className="h-4 w-4" />
                      <span>{staffMember.telephone}</span>
                    </div>
                    <div className="flex items-start gap-2 text-sm text-muted-foreground">
                      <MapPin className="h-4 w-4 mt-0.5" />
                      <span className="line-clamp-2">{staffMember.address}</span>
                    </div>
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                      <Calendar className="h-4 w-4" />
                      <span>Updated {new Date(staffMember.lastUpdated).toLocaleDateString()}</span>
                    </div>
                    <div className="flex gap-2 pt-2">
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => openViewDialog(staffMember)}
                        className="flex-1"
                      >
                        <Eye className="h-4 w-4 mr-1" />
                        View
                      </Button>
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => openEditDialog(staffMember)}
                        className="flex-1"
                      >
                        <Edit2 className="h-4 w-4 mr-1" />
                        Edit
                      </Button>
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => openDeleteDialog(staffMember)}
                        className="text-destructive hover:text-destructive"
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </CardContent>
                </Card>
              ))
            ) : (
              <div className="col-span-full text-center py-12">
                <p className="text-muted-foreground">
                  {searchTerm ? 'No staff found matching your search.' : 'No staff found.'}
                </p>
              </div>
            )}
          </div>
 
        </>
      )}

      {/* Add Staff Dialog */}
      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Add New Staff Member</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleAddStaff} className="space-y-4">
            <div>
              <Label htmlFor="staffName">Full Name</Label>
              <Input
                id="staffName"
                value={newStaff.name}
                onChange={(e) => setNewStaff({ ...newStaff, name: e.target.value })}
                placeholder="Enter full name"
                required
              />
            </div>
            <div>
              <Label htmlFor="staffTelephone">Telephone</Label>
              <Input
                id="staffTelephone"
                value={newStaff.telephone}
                onChange={(e) => setNewStaff({ ...newStaff, telephone: e.target.value })}
                placeholder="Enter telephone number"
                required
              />
            </div>
            <div>
              <Label htmlFor="staffAddress">Address</Label>
              <Input
                id="staffAddress"
                value={newStaff.address}
                onChange={(e) => setNewStaff({ ...newStaff, address: e.target.value })}
                placeholder="Enter address"
                required
              />
            </div>
            <div>
              <Label htmlFor="staffUsername">Username</Label>
              <Input
                id="staffUsername"
                value={newStaff.username}
                onChange={(e) => setNewStaff({ ...newStaff, username: e.target.value })}
                placeholder="Enter username"
                required
              />
            </div>
            <div>
              <Label htmlFor="staffEmail">Email</Label>
              <Input
                id="staffEmail"
                type="email"
                value={newStaff.email}
                onChange={(e) => setNewStaff({ ...newStaff, email: e.target.value })}
                placeholder="Enter email address"
                required
              />
            </div>
            <div>
              <Label htmlFor="staffPassword">Password</Label>
              <Input
                id="staffPassword"
                type="password"
                value={newStaff.password}
                onChange={(e) => setNewStaff({ ...newStaff, password: e.target.value })}
                placeholder="Enter password"
                required
              />
            </div>
            <div>
              <Label htmlFor="staffRole">Role</Label>
              <Select value={newStaff.role} onValueChange={(value) => setNewStaff({ ...newStaff, role: value == "MANAGER" ? "MANAGER" :  "CASHIER"  })}>
                <SelectTrigger>
                  <SelectValue placeholder="Select role" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="MANAGER">Manager</SelectItem>
                  <SelectItem value="CASHIER">Cashier</SelectItem> 
                </SelectContent>
              </Select>
            </div>
            <div className="flex gap-2 pt-4">
              <Button type="button" variant="outline" onClick={() => setIsDialogOpen(false)} className="flex-1">
                Cancel
              </Button>
              <Button type="submit" className="flex-1">
                Add Staff
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>
 
  <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Edit Staff Member</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleEditStaff} className="space-y-4">
            <div>
              <Label htmlFor="editStaffName">Full Name</Label>
              <Input
                id="editStaffName"
                value={newStaff.name}
                onChange={(e) => setNewStaff({ ...newStaff, name: e.target.value })}
                placeholder="Enter full name"
                required
              />
            </div>
            <div>
              <Label htmlFor="editStaffTelephone">Telephone</Label>
              <Input
                id="editStaffTelephone"
                value={newStaff.telephone}
                onChange={(e) => setNewStaff({ ...newStaff, telephone: e.target.value })}
                placeholder="Enter telephone number"
                required
              />
            </div>
            <div>
              <Label htmlFor="editStaffAddress">Address</Label>
              <Input
                id="editStaffAddress"
                value={newStaff.address}
                onChange={(e) => setNewStaff({ ...newStaff, address: e.target.value })}
                placeholder="Enter address"
                required
              />
            </div>
            <div>
              <Label htmlFor="editStaffUsername">Username</Label>
              <Input
                id="editStaffUsername"
                value={newStaff.username}
                onChange={(e) => setNewStaff({ ...newStaff, username: e.target.value })}
                placeholder="Enter username"
                required
              />
            </div>
            <div>
              <Label htmlFor="editStaffEmail">Email</Label>
              <Input
                id="editStaffEmail"
                type="email"
                value={newStaff.email}
                onChange={(e) => setNewStaff({ ...newStaff, email: e.target.value })}
                placeholder="Enter email address"
                required
              />
            </div>
            <div>
              <Label htmlFor="editStaffPassword">Password (leave empty to keep current)</Label>
              <Input
                id="editStaffPassword"
                type="password"
                value={newStaff.password}
                onChange={(e) => setNewStaff({ ...newStaff, password: e.target.value })}
                placeholder="Enter new password"
              />
            </div>
            <div>
              <Label htmlFor="editStaffRole">Role</Label>
              <Select value={newStaff.role} onValueChange={(value) => setNewStaff({ ...newStaff, role: value == "MANAGER" ? "MANAGER" :  "CASHIER"  })}>
                <SelectTrigger>
                  <SelectValue placeholder="Select role" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="MANAGER">Manager</SelectItem>
                  <SelectItem value="CASHIER">Cashier</SelectItem>
                  <SelectItem value="ASSISTANT">Assistant</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="flex gap-2 pt-4">
              <Button type="button" variant="outline" onClick={() => setIsEditDialogOpen(false)} className="flex-1">
                Cancel
              </Button>
              <Button type="submit" className="flex-1">
                Update Staff
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>

      {/* View Staff Dialog */}
      <Dialog open={isViewDialogOpen} onOpenChange={setIsViewDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Staff Member Details</DialogTitle>
          </DialogHeader>
          {selectedStaff && (
            <div className="space-y-4">
              <div>
                <Label className="text-sm font-medium text-muted-foreground">Full Name</Label>
                <p className="text-base">{selectedStaff.name}</p>
              </div>
              <div>
                <Label className="text-sm font-medium text-muted-foreground">Username</Label>
                <p className="text-base">{selectedStaff.username}</p>
              </div>
              <div>
                <Label className="text-sm font-medium text-muted-foreground">Email</Label>
                <p className="text-base">{selectedStaff.email}</p>
              </div>
              <div>
                <Label className="text-sm font-medium text-muted-foreground">Telephone</Label>
                <p className="text-base">{selectedStaff.telephone}</p>
              </div>
              <div>
                <Label className="text-sm font-medium text-muted-foreground">Address</Label>
                <p className="text-base">{selectedStaff.address}</p>
              </div>
              <div>
                <Label className="text-sm font-medium text-muted-foreground">Role</Label>
                <div className="mt-1">
                  <Badge variant={getRoleBadgeVariant(selectedStaff.role)}>
                    {selectedStaff.role}
                  </Badge>
                </div>
              </div>
              <div>
                <Label className="text-sm font-medium text-muted-foreground">Status</Label>
                <div className="mt-1">
                  <Badge variant={selectedStaff.isActive ? "default" : "secondary"}>
                    {selectedStaff.isActive ? "Active" : "Inactive"}
                  </Badge>
                </div>
              </div>
              <div>
                <Label className="text-sm font-medium text-muted-foreground">Last Updated</Label>
                <p className="text-base">{new Date(selectedStaff.lastUpdated).toLocaleString()}</p>
              </div>
              <div className="flex gap-2 pt-4">
                <Button type="button" variant="outline" onClick={() => setIsViewDialogOpen(false)} className="flex-1">
                  Close
                </Button>
                <Button 
                  onClick={() => {
                    setIsViewDialogOpen(false);
                    openEditDialog(selectedStaff);
                  }}
                  className="flex-1"
                >
                  Edit Staff
                </Button>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>
 
 <AlertDialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Are you sure?</AlertDialogTitle>
            <AlertDialogDescription>
              This action cannot be undone. This will permanently delete the staff member
              {selectedStaff && ` "${selectedStaff.name}"`} and remove all associated data.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction onClick={handleDeleteStaff} className="bg-destructive hover:bg-destructive/90">
              Delete
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
};

export { StaffManagement };